/*
 * Implementation of a read-write lock,
 * as covered in class notes.
 */

package com.zachfreeman;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RWL {
    int readers, writers, waitingReaders, waitingWriters;
    final ReentrantLock lock = new ReentrantLock();
    final Condition readable = lock.newCondition();
    final Condition writeable = lock.newCondition();

    void lockRead() {
        lock.lock();
        try {
            while(writers != 0) {
                ++waitingReaders;
                readable.awaitUninterruptibly();
                --waitingReaders;
            }
            ++readers;
        } finally {
            lock.unlock();
        }
    }

    void lockWrite() {
        lock.lock();
        try {
            while(writers != 0 || readers != 0) {
                ++waitingWriters;
                writeable.awaitUninterruptibly();
                --waitingWriters;
            }
            writers = 1;
        } finally {
            lock.unlock();
        }
    }

    void unlockRead() {
        lock.lock();
        try {
            if(--readers == 0)
                if(waitingWriters != 0)
                    writeable.signal();
                else
                    readable.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void unlockWrite() {
        lock.lock();
        try {
            writers = 0;
            if(waitingWriters != 0)
                writeable.signal();
            else
                readable.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
