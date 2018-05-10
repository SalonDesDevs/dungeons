package net.wytrem.jameo.utils;

/**
 * Extrapolator maintains state about updates for remote entities, and
 * will generate smooth guesses about where those entities will be
 * based on previously received data.
 * <p>
 * You create one Extrapolator per quantity you want to interpolate.
 * Arguments include the type to interpolate (works best with float
 * or double), and how many values/axes (typically 3 for positions).
 * <p>
 * You then feed it updates about where your entity was ("positions")
 * at some point in time. Optionally, you can also give the Extrapolator
 * information about the velocity at that point in time.
 * After that, you can ask the Extrapolator for position values for
 * some given real time. Optionally, you can also ask for the velocity
 * at the same time.
 * <p>
 * The Extrapolator will assume that an entity stops in place if it
 * hasn't received updates for some time about the entity. It will also
 * keep a running estimate of the latency and frequency of updates.
 * <p>
 * Extrapolator requires a globally synchronized clock. It does not do
 * any management to deal with clock skew (that is the subject of a
 * separate class).
 */
public abstract class FloatExtrapolator {
    private float[] _snapPos, _snapVel, _aimPos, _lastPacketPos;
    private float[] _tmpArr, _tmpArr2, _tmpArr3;
    private double _snapTime, _aimTime, _lastPacketTime, _latency, _updateTime;
    private int _size;

    public FloatExtrapolator() {
        this(1);
    }

    public FloatExtrapolator(int size) {
        _size = size;
        _snapPos = createArray();
        _snapVel = createArray();
        _aimPos = createArray();
        _lastPacketPos = createArray();
        _tmpArr = createArray();
        _tmpArr2 = createArray();
        _tmpArr3 = createArray();
    }

    /**
     * When you receive data about a remote entity, call AddSample() to
     * tell the Extrapolator about it. The Extrapolator will throw away a
     * sample with a time that's before any other time it's already gotten,
     * and return false; else it will use the sample to improve its
     * interpolation guess, and return true.
     *
     * @param packetTime The globally synchronized time at which the
     *                   packet was sent (and thus the data valid).
     * @param curTime    The globally synchronized time at which you put
     *                   the data into the Extrapolator (i e, "now").
     * @param pos        The position sample valid for packetTime.
     * @return true if packetTime is strictly greater than the previous
     * packetTime, else false.
     */
    public boolean addSample(double packetTime, double curTime, float[] pos) {
        // The best guess I can make for velocity is the difference between
        // this sample and the last registered sample.
        float[] vel = _tmpArr2;
        if (Math.abs(packetTime - _lastPacketTime) > 1e-4) {
            double dt = 1.0 / (packetTime - _lastPacketTime);
            for (int i = 0; i < _size; ++i) {
                // vel[i] = (pos[i] - _lastPacket[i]) * dt
                vel[i] = (float) ((pos[i] - _lastPacketPos[i]) * dt);
            }
        } else {
            clear(vel);
        }

        return addSample(packetTime, curTime, pos, vel);
    }

    /**
     * When you receive data about a remote entity, call AddSample() to
     * tell the Extrapolator about it. The Extrapolator will throw away a
     * sample with a time that's before any other time it's already gotten,
     * and return false; else it will use the sample to improve its
     * interpolation guess, and return true.
     * <p>
     * If you get velocity information with your position updates, you can
     * make the guess that Extrapolator makes better, by passing that
     * information along with your position sample.
     *
     * @param packetTime The globally synchronized time at which the
     *                   packet was sent (and thus the data valid).
     * @param curTime    The globally synchronized time at which you put
     *                   the data into the Extrapolator (i e, "now").
     * @param pos        The position sample valid for packetTime.
     * @param vel        The velocity of the entity at the time of packetTime.
     *                   Used to improve the guess about entity position.
     * @return true if packetTime is strictly greater than the previous
     * packetTime, else false.
     */
    public boolean addSample(double packetTime, double curTime, float[] pos, float[] vel) {
        if (!estimates(packetTime, curTime)) {
            return false;
        }

        copyArray(_lastPacketPos, pos);
        _lastPacketTime = packetTime;
        readPosition(curTime, _snapPos);
        _aimTime = curTime + _updateTime;
        double dt = _aimTime - packetTime;
        _snapTime = curTime;
        for (int i = 0; i < _size; ++i) {
            // _aimPos[i] = pos[i] + vel[i] * dt
            _aimPos[i] = (float) (pos[i] + vel[i] * dt);
        }

        // I now have two positions and two times:
        //   1. _aimPos / _aimTime
        //   2. _snapPos / _snapTime
        // I must generate the interpolation velocity based on these two samples.
        // However, if _aimTime is the same as _snapTime, I'm in trouble.
        // In that case, use the supplied velocity.
        if (Math.abs(_aimTime - _snapTime) < 1e-4) {
            copyArray(_snapVel, vel);
        } else {
            dt = 1.0 / (_aimTime - _snapTime);
            for (int i = 0; i < _size; ++i) {
                _snapVel[i] = (float) ((_aimPos[i] - _snapPos[i]) * dt);
            }
        }

        return true;
    }

    /**
     * Re-set the Extrapolator's idea of time, velocity and position.
     * The velocity will be re-set to 0.
     *
     * @param packetTime The packet time to reset to.
     * @param curTime    The current time to reset to.
     * @param pos        The position to reset to.
     */
    public void reset(double packetTime, double curTime, float[] pos) {
        reset(packetTime, curTime, pos, clear(_tmpArr));
    }

    /**
     * Re-set the Extrapolator's idea of time, velocity and position.
     *
     * @param packetTime The packet time to reset to.
     * @param curTime    The current time to reset to.
     * @param pos        The position to reset to.
     * @param vel        The velocity to reset to.
     */
    public void reset(double packetTime, double curTime, float[] pos, float[] vel) {
        assert (packetTime <= curTime);

        _lastPacketTime = packetTime;
        copyArray(_lastPacketPos, pos);
        _snapTime = curTime;
        copyArray(_snapPos, pos);
        _updateTime = curTime - packetTime;
        _latency = _updateTime;
        _aimTime = curTime + _updateTime;
        copyArray(_snapVel, vel);

        for (int i = 0; i < _size; ++i) {
            // _aimPos[i] = _snapPos[i] + _snapVel[i] * _updateTime
            _aimPos[i] = (float) (_snapPos[i] + _snapVel[i] * _updateTime);
        }
    }

    /**
     * Return an estimate of the interpolated position at a given global
     * time (which typically will be greater than the curTime passed into
     * AddSample()).
     *
     * @param forTime The time at which to interpolate the entity. It should
     *                be greater than the last packetTime, and less than the last curTime
     *                plus some allowable slop (determined by EstimateFreqency()).
     * @param outPos  The interpolated position for the given time.
     * @return false if forTime is out of range (at which point the oPos
     * will still make sense, but movement will be clamped); true when forTime
     * is within range.
     */
    public boolean readPosition(double forTime, float[] outPos) {
        return readPosition(forTime, outPos, null);
    }

    /**
     * Return an estimate of the interpolated position at a given global
     * time (which typically will be greater than the curTime passed into
     * AddSample()).
     *
     * @param forTime The time at which to interpolate the entity. It should
     *                be greater than the last packetTime, and less than the last curTime
     *                plus some allowable slop (determined by EstimateFreqency()).
     * @param outPos  The interpolated position for the given time.
     * @param outVel  The interpolated velocity for the given time.
     * @return false if forTime is out of range (at which point the oPos
     * will still make sense, but movement will be clamped); true when forTime
     * is within range.
     */
    public boolean readPosition(double forTime, float[] outPos, float[] outVel) {
        boolean isOk = true;

        // asking for something before allowable time?
        if (forTime < _snapTime) {
            forTime = _snapTime;
            isOk = false;
        }

        // asking for something very far in the future?
        double maxRange = _aimTime + _updateTime;
        if (forTime > maxRange) {
            forTime = maxRange;
            isOk = false;
        }

        // calculate the interpolated position
        for (int i = 0; i < _size; ++i) {
            if (outVel != null) {
                outVel[i] = _snapVel[i];
            }

            // outPos[i] = _snapPos[i] + _snapVel[i] * (forTime - _snapTime)
            outPos[i] = (float) (_snapPos[i] + _snapVel[i] * (forTime - _snapTime));
        }

        if (!isOk && outVel != null) {
            clear(outVel);
        }

        return isOk;
    }

    public double estimateLatency() {
        return _latency;
    }

    public double estimateUpdateTime() {
        return _updateTime;
    }

    private boolean estimates(double packet, double cur) {
        if (packet <= _lastPacketTime) {
            return false;
        }

        // The theory is that, if latency increases, quickly
        // compensate for it, but if latency decreases, be a
        // little more resilient; this is intended to compensate
        // for jittery delivery.
        double lat = cur - packet;
        if (lat < 0) {
            lat = 0;
        }
        if (lat > _latency) {
            _latency = (_latency + lat) * 0.5;
        } else {
            _latency = (_latency * 7 + lat) * 0.125;
        }

        // Do the same running average for update time.
        // Again, the theory is that a lossy connection wants
        // an average of a higher update time.
        double tick = packet - _lastPacketTime;
        if (tick > _updateTime) {
            _updateTime = (_updateTime + tick) * 0.5;
        } else {
            _updateTime = (_updateTime * 7 + tick) * 0.125;
        }

        return true;
    }

    private float[] clear(float[] arr) {
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = 0.0f;
        }
        return arr;
    }

    private float[] createArray() {
        return new float[_size];
    }

    private void copyArray(float[] dest, float[] src) {
        System.arraycopy(src, 0, dest, 0, src.length);
    }
}
