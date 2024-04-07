package com.yuanstack.xrpc.core.governance;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sylvan
 * @date 2024/03/31  16:10
 */
@Getter
@ToString
@Slf4j
public class SlidingTimeWindow {

    public static final int DEFAULT_SIZE = 30;

    private final int size;
    private final RingBuffer ringBuffer;
    private int sum = 0;

    //    private int _start_mark = -1;
//    private int _prev_mark  = -1;
    private int _curr_mark = -1;

    private long _start_ts = -1L;
    //   private long _prev_ts  = -1L;
    private long _curr_ts = -1L;

    public SlidingTimeWindow() {
        this(DEFAULT_SIZE);
    }

    public SlidingTimeWindow(int _size) {
        this.size = _size;
        this.ringBuffer = new RingBuffer(this.size);
    }

    /**
     * record current ts millis.
     */
    public synchronized void record(long millis) {
        log.debug("window before is [{}]", this);
        log.debug("window add record ({})", millis);
        long ts = millis / 1000;
        if (_start_ts == -1L) {
            initRing(ts);
        } else {   // TODO  Prev 是否需要考虑
            if (ts == _curr_ts) {
                log.debug("window ts is [{}], curr_ts is [{}], size is [{}]", ts, _curr_ts, size);
                this.ringBuffer.incr(_curr_mark, 1);
            } else if (ts > _curr_ts && ts < _curr_ts + size) {
                int offset = (int) (ts - _curr_ts);
                log.debug("window ts is [{}], curr_ts is [{}], size is [{}], offset is [{}]", ts, _curr_ts, size, offset);
                this.ringBuffer.reset(_curr_mark + 1, offset);
                this.ringBuffer.incr(_curr_mark + offset, 1);
                _curr_ts = ts;
                _curr_mark = (_curr_mark + offset) % size;
            } else if (ts >= _curr_ts + size) {
                log.debug("window ts is [{}], curr_ts is [{}], size is [{}]", ts, _curr_ts, size);
                this.ringBuffer.reset();
                initRing(ts);
            }
        }
        this.sum = this.ringBuffer.sum();
        log.debug("window after is [{}]", this);
    }

    private void initRing(long ts) {
        log.debug("window initRing ts is [{}]", ts);
        this._start_ts = ts;
        this._curr_ts = ts;
        this._curr_mark = 0;
        this.ringBuffer.incr(0, 1);
    }

}
