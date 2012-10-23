package ch.cern.atlas.apvs.ptu.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.cern.atlas.apvs.domain.APVSException;
import ch.cern.atlas.apvs.domain.Event;
import ch.cern.atlas.apvs.domain.Measurement;
import ch.cern.atlas.apvs.domain.Ptu;
import ch.cern.atlas.apvs.domain.Report;

public class PtuSimulator extends Thread {

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	private final Channel channel;
	private final Random random = new Random();
	private final int defaultWait;
	private final int extraWait;
	private final int deltaStartTime = 12 * 3600 * 1000;
	private final String ptuId;
	private Ptu ptu;
	
	private final static boolean WRITE_MARKERS = true;

	public PtuSimulator(String ptuId, int refresh) {
		this(ptuId, refresh, null);
	}

	public PtuSimulator(String ptuId, int refresh, Channel channel) {
		this.defaultWait = refresh;
		this.extraWait = refresh / 3;
		this.channel = channel;
		this.ptuId = ptuId;
	}

	@Override
	public void run() {
		try {
			long now = new Date().getTime();
			long then = now - deltaStartTime;
			Date start = new Date(then);

			ptu = new Ptu(ptuId.toString());

			try {
				ptu.addMeasurement(new Temperature(ptuId, 25.7, start));
				ptu.addMeasurement(new Humidity(ptuId, 31.4, start));
				ptu.addMeasurement(new CO2(ptuId, 2.5, start));
				ptu.addMeasurement(new BodyTemperature(ptuId, 37.2, start));
				ptu.addMeasurement(new HeartRate(ptuId, 120, start));
				ptu.addMeasurement(new DoseAccum(ptuId, 0.042, start));
				ptu.addMeasurement(new DoseRate(ptuId, 0.001, start));
				ptu.addMeasurement(new O2(ptuId, 85.2, start));
			} catch (APVSException e) {
				log.warn("Could not add measurement", e);
			}
			log.info(ptuId);

			then += defaultWait + random.nextInt(extraWait);

			OutputStream os;
			if (channel != null) {
				ChannelBuffer buffer = ChannelBuffers.buffer(8192);
				os = new ChannelBufferOutputStream(buffer);
			} else {
				os = new ByteArrayOutputStream();
			}

			int i = 1;

			try {
				// now loop at current time
				while (!isInterrupted()) {
					@SuppressWarnings("resource")
					PtuJsonWriter writer = new PtuJsonWriter(os);
					if (WRITE_MARKERS) {
						writer.write(0x10);
					}
					if (i % 5 == 0) {
						Event event = nextEvent(ptu, new Date());
						writer.write(event);
					} else {
						writer.write(nextMeasurement(ptu, new Date()));
					}
					writer.newLine();
					if (WRITE_MARKERS) {
						writer.write(0x13);
					}
					writer.flush();

					os = sendBufferAndClear(os);
					Thread.sleep(defaultWait + random.nextInt(extraWait));
					System.out.print(".");
					System.out.flush();
					i++;
				}
			} catch (InterruptedException e) {
				// ignored
			}
			System.err.print("*");
			System.out.flush();
		} catch (IOException e) {
			// ignored
		} finally {
			if (channel != null) {
				log.info("Closing");
				channel.close();
			}
		}
	}

	protected synchronized OutputStream sendBufferAndClear(OutputStream os) {
		if (channel != null) {
			ChannelBufferOutputStream cos = (ChannelBufferOutputStream) os;
			channel.write(cos.buffer()).awaitUninterruptibly();
			cos.buffer().clear();
		} else {
			os = new ByteArrayOutputStream();
		}
		return os;
	}

	private Measurement nextMeasurement(Ptu ptu, Date d) {
		int index = random.nextInt(ptu.getSize());
		String name = ptu.getMeasurementNames().get(index);
		Measurement measurement = nextMeasurement(ptu.getMeasurement(name), d);
		try {
			ptu.addMeasurement(measurement);
		} catch (APVSException e) {
			log.warn("Could not add measurement", e);
		}
		return measurement;
	}

	private Measurement nextMeasurement(Measurement m, Date d) {
		return new Measurement(m.getPtuId(), m.getName(), m.getValue()
				.doubleValue() + random.nextGaussian(), m.getUnit(), d);
	}

	@SuppressWarnings("unused")
	private Report nextReport(Ptu ptu, Date d) {
		return new Report(ptu.getPtuId(), random.nextGaussian(),
				random.nextBoolean(), random.nextBoolean(),
				random.nextBoolean(), d);
	}

	private Event nextEvent(Ptu ptu, Date d) {
		int index = random.nextInt(ptu.getSize());
		String name = ptu.getMeasurementNames().get(index);
		double d1 = random.nextDouble();
		double d2 = random.nextDouble();

		return new Event(ptu.getPtuId(), name, "UpLevel", Math.max(d1, d2),
				Math.min(d1, d2), d);
	}
}
