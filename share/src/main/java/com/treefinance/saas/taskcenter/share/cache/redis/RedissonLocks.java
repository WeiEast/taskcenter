/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.share.cache.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Jerry
 * @since 10:11 04/08/2017
 */
@Component
public class RedissonLocks {

    private final RedissonClient redisson;

    @Autowired
    public RedissonLocks(RedissonClient redisson) {
        this.redisson = Objects.requireNonNull(redisson);
    }

    public void lock(@Nonnull final String key, @Nonnull final Runnable runnable) {
        RLock lock = redisson.getLock(key);

        try {
            lock.lock();

            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public <V> V lock(@Nonnull final String key, @Nonnull final Callable<V> callable) throws Exception {
        RLock lock = redisson.getLock(key);

        try {
            lock.lock();

            return callable.call();
        } finally {
            lock.unlock();
        }
    }

    public void lockFair(@Nonnull final String key, @Nonnull final Runnable runnable) {
        RLock lock = redisson.getFairLock(key);

        try {
            lock.lock();

            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public <V> V lockFair(@Nonnull final String key, @Nonnull final Callable<V> callable) throws Exception {
        RLock lock = redisson.getFairLock(key);

        try {
            lock.lock();

            return callable.call();
        } finally {
            lock.unlock();
        }
    }

    public void tryLock(@Nonnull final String key, @Nonnull final Runnable runnable) {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock();

            if (isLock) {
                runnable.run();
            }
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLock(@Nonnull final String key, @Nonnull final Consumer<Boolean> consumer) {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock();

            consumer.accept(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public <V> V tryLock(@Nonnull final String key, @Nonnull final Function<Boolean, V> function) {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock();

            return function.apply(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLock(@Nonnull final String key, long waitTime, @Nonnull TimeUnit unit, @Nonnull final Runnable runnable) throws InterruptedException {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, unit);

            if (isLock) {
                runnable.run();
            }
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLock(@Nonnull final String key, long waitTime, @Nonnull TimeUnit unit, @Nonnull final Consumer<Boolean> consumer) throws InterruptedException {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, unit);

            consumer.accept(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public <V> V tryLock(@Nonnull final String key, long waitTime, @Nonnull TimeUnit unit, @Nonnull final Function<Boolean, V> function) throws InterruptedException {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, unit);

            return function.apply(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLock(@Nonnull final String key, long waitTime, long leaseTime, @Nonnull TimeUnit unit, @Nonnull final Runnable runnable) throws InterruptedException {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, leaseTime, unit);

            if (isLock) {
                runnable.run();
            }
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLock(@Nonnull final String key, long waitTime, long leaseTime, @Nonnull TimeUnit unit, @Nonnull final Consumer<Boolean> consumer) throws InterruptedException {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, leaseTime, unit);

            consumer.accept(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public <V> V tryLock(@Nonnull final String key, long waitTime, long leaseTime, @Nonnull TimeUnit unit, @Nonnull final Function<Boolean, V> function)
        throws InterruptedException {
        RLock lock = redisson.getLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, leaseTime, unit);

            return function.apply(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLockFair(@Nonnull final String key, @Nonnull final Runnable runnable) {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock();

            if (isLock) {
                runnable.run();
            }
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLockFair(@Nonnull final String key, @Nonnull final Consumer<Boolean> consumer) {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock();

            consumer.accept(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public <V> V tryLockFair(@Nonnull final String key, @Nonnull final Function<Boolean, V> function) {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock();

            return function.apply(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLockFair(@Nonnull final String key, long waitTime, @Nonnull TimeUnit unit, @Nonnull final Runnable runnable) throws InterruptedException {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, unit);

            if (isLock) {
                runnable.run();
            }
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLockFair(@Nonnull final String key, long waitTime, @Nonnull TimeUnit unit, @Nonnull final Consumer<Boolean> consumer) throws InterruptedException {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, unit);

            consumer.accept(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public <V> V tryLockFair(@Nonnull final String key, long waitTime, @Nonnull TimeUnit unit, @Nonnull final Function<Boolean, V> function) throws InterruptedException {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, unit);

            return function.apply(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLockFair(@Nonnull final String key, long waitTime, long leaseTime, @Nonnull TimeUnit unit, @Nonnull final Runnable runnable) throws InterruptedException {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, leaseTime, unit);

            if (isLock) {
                runnable.run();
            }
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public void tryLockFair(@Nonnull final String key, long waitTime, long leaseTime, @Nonnull TimeUnit unit, @Nonnull final Consumer<Boolean> consumer)
        throws InterruptedException {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, leaseTime, unit);

            consumer.accept(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

    public <V> V tryLockFair(@Nonnull final String key, long waitTime, long leaseTime, @Nonnull TimeUnit unit, @Nonnull final Function<Boolean, V> function)
        throws InterruptedException {
        RLock lock = redisson.getFairLock(key);

        boolean isLock = false;
        try {
            isLock = lock.tryLock(waitTime, leaseTime, unit);

            return function.apply(isLock);
        } finally {
            if (isLock) {
                lock.unlock();
            }
        }
    }

}
