package com.canseverayberk.samples.redissonpriorityqueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class RedissonPriorityQueueApplication {

	private RedissonClient redisson;
	private RPriorityQueue<Entry> queue;

	public static void main(String[] args) {
		SpringApplication.run(RedissonPriorityQueueApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Config config = new Config();
		config.useSentinelServers()
				.setCheckSentinelsList(false)
				.setMasterName("mymaster")
				.addSentinelAddress("redis://127.0.0.1:26379");

		redisson = Redisson.create(config);
		queue = redisson.getPriorityQueue("priorityQueue");

		new Thread(new EnqueuThread(queue)).start();
		new Thread(new DequeuThread(queue)).start();
	}

	@Data
	@AllArgsConstructor
	class EnqueuThread implements Runnable {

		private RPriorityQueue<Entry> queue;

		@SneakyThrows
		@Override
		public void run() {
			while (true) {
				try {
					Entry entry = new Entry(UUID.randomUUID().toString(), new Random().nextInt(100));
					queue.add(entry);
					log.info("Enqueue: {}", entry);
				} finally {
					TimeUnit.SECONDS.sleep(1);
				}
			}
		}
	}

	@Data
	@AllArgsConstructor
	class DequeuThread implements Runnable {

		private RPriorityQueue<Entry> queue;

		@SneakyThrows
		@Override
		public void run() {
			while (true) {
				try {
					Entry entry = queue.poll();
					if (Objects.isNull(entry)) {
						continue;
					}
					log.info("Dequeue: {}", entry);
				} finally {
					TimeUnit.SECONDS.sleep(4);
				}
			}
		}
	}
}
