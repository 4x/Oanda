package org.pminin.tb;

import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.pminin.tb.constants.Constants;
import org.pminin.tb.constants.Event;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

@Component(value = "scheduler")
public class Scheduler implements Constants {

	protected static final Config config = ConfigFactory.load().getConfig("main");
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
	private ActorSystem actorSystem;
	private boolean active = false;

	private boolean forceStartOfDay = true;

	@Autowired
	public Scheduler(ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
	}

	@Scheduled(cron = "${main.scheduler.trade-check.cron}")
	public void fireCheckTrades() {
		if (!active || actorSystem == null) {
			return;
		}
		actorSystem.actorSelection(Constants.ACTOR_PATH_HEAD + "*/" + Constants.COLLECTOR)
				.tell(Event.CHECK_TRADES_ORDERS, actorSystem.guardian());
	}

	@Scheduled(cron = "${main.scheduler.candle-collect.cron}")
	public void fireCollectCandles() {
		if (!active || actorSystem == null) {
			return;
		}
		actorSystem.actorSelection(Constants.ACTOR_PATH_HEAD + "*/" + Constants.COLLECTOR).tell(Event.COLLECT_CANDLES,
				actorSystem.guardian());
	}

	@Scheduled(cron = "${main.scheduler.pivot-collect.cron}")
	public void fireCollectPivot() {
		if (!active || actorSystem == null) {
			return;
		}
		actorSystem.actorSelection(Constants.ACTOR_PATH_HEAD + "*/" + Constants.COLLECTOR).tell(Event.COLLECT_PIVOT,
				actorSystem.guardian());
	}

	@PostConstruct
	public void fireInitialCollection() {
		new ScheduledThreadPoolExecutor(1).schedule(() -> {

			forceStartOfDay = isNotFalse("forcestartofday");
			active = isTrue("autostart") || forceStartOfDay;
			if (active) {
				log.info("Autostart set to TRUE. Start collecting candles...");
			}
			try {
				CronExpression expression = new CronExpression(
						forceStartOfDay ? config.getString("scheduler.forced-work.cron")
								: config.getString("scheduler.start-work.cron"));
				log.info("Trading will start at " + expression.getNextValidTimeAfter(new Date()));
			} catch (Exception e) {
				log.info("Something went wrong while parsing cron expression. I will not say when trading will start");
			}
		}, 15, TimeUnit.SECONDS);
	}

	@Scheduled(cron = "${main.scheduler.forced-work.cron}")
	public void forceStart() {
		if (!active || actorSystem == null) {
			return;
		}
		if (forceStartOfDay) {
			forceStartOfDay = false;
			startWorkEveryDay();
		}
	}

	private boolean isNotFalse(String path) {
		return !config.hasPath(path) || config.getBoolean(path);
	}

	private boolean isTrue(String path) {
		return config.hasPath(path) && config.getBoolean(path);
	}

	public void resetState() {
		actorSystem.actorSelection(Constants.ACTOR_PATH_HEAD + "*/" + Constants.STRATEGY).tell(Event.TRADE_CLOSED,
				actorSystem.guardian());
	}

	@Scheduled(cron = "${main.scheduler.start-work.cron}")
	public void startWorkEveryDay() {
		// Start work
		if (!active || actorSystem == null) {
			return;
		}
		fireCollectPivot();
		new ScheduledThreadPoolExecutor(1).schedule(() -> {
			actorSystem.actorSelection(Constants.ACTOR_PATH_HEAD + "*/" + Constants.STRATEGY).tell(Event.WORK,
					actorSystem.guardian());
		}, 15, TimeUnit.SECONDS);
	}

	@Scheduled(cron = "${main.scheduler.end-work.cron}")
	public void tgiFriday() {
		// End work
		if (!active || actorSystem == null) {
			return;
		}
		actorSystem.actorSelection(Constants.ACTOR_PATH_HEAD + "*/" + Constants.STRATEGY).tell(Event.TGI_FRIDAY,
				actorSystem.guardian());
	}

}
