package com.store.popup.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 분산 락 서비스
 *
 * 여러 서버 인스턴스가 동시에 실행되는 환경에서 안전한 동시성 제어를 제공합니다.
 *
 * 사용 예시:
 * <pre>
 * String result = distributedLockService.executeWithLock(
 *     "view-history:member:123:post:456",
 *     () -> recordViewHistoryLogic(),
 *     3000,  // waitTime: 락 획득 대기 시간 (ms)
 *     5000   // leaseTime: 락 자동 해제 시간 (ms)
 * );
 * </pre>
 *
 * @author Claude Code
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedissonClient redissonClient;

    /**
     * 분산 락을 획득하고 작업을 실행합니다.
     *
     * @param lockKey 락의 고유 식별자 (예: "view-history:member:1:post:100")
     * @param supplier 락 획득 후 실행할 작업
     * @param waitTime 락 획득을 기다리는 최대 시간 (밀리초)
     * @param leaseTime 락을 자동으로 해제하는 시간 (밀리초, 데드락 방지)
     * @param <T> 반환 타입
     * @return 작업 실행 결과
     * @throws IllegalStateException 락 획득 실패 시
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 락 획득 시도
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);

            if (!acquired) {
                log.warn("분산 락 획득 실패: lockKey={}, waitTime={}ms", lockKey, waitTime);
                throw new IllegalStateException("분산 락을 획득할 수 없습니다: " + lockKey);
            }

            log.debug("분산 락 획득 성공: lockKey={}", lockKey);

            // 락을 획득한 상태에서 작업 실행
            return supplier.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("분산 락 획득 중 인터럽트 발생: lockKey={}", lockKey, e);
            throw new IllegalStateException("분산 락 획득 중 인터럽트 발생", e);
        } finally {
            // 락 해제 (현재 스레드가 락을 소유하고 있는 경우에만)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("분산 락 해제: lockKey={}", lockKey);
            }
        }
    }

    /**
     * 분산 락을 획득하고 작업을 실행합니다 (반환값 없음).
     *
     * @param lockKey 락의 고유 식별자
     * @param runnable 락 획득 후 실행할 작업
     * @param waitTime 락 획득을 기다리는 최대 시간 (밀리초)
     * @param leaseTime 락을 자동으로 해제하는 시간 (밀리초)
     */
    public void executeWithLock(String lockKey, Runnable runnable, long waitTime, long leaseTime) {
        executeWithLock(lockKey, () -> {
            runnable.run();
            return null;
        }, waitTime, leaseTime);
    }

    /**
     * 기본 타임아웃으로 분산 락을 실행합니다.
     * - waitTime: 3초
     * - leaseTime: 5초
     *
     * @param lockKey 락의 고유 식별자
     * @param supplier 락 획득 후 실행할 작업
     * @param <T> 반환 타입
     * @return 작업 실행 결과
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, supplier, 3000L, 5000L);
    }

    /**
     * 기본 타임아웃으로 분산 락을 실행합니다 (반환값 없음).
     *
     * @param lockKey 락의 고유 식별자
     * @param runnable 락 획득 후 실행할 작업
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        executeWithLock(lockKey, runnable, 3000L, 5000L);
    }
}
