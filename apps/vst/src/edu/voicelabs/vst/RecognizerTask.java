package edu.voicelabs.vst;

import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.pocketsphinx;

/**
 * Speech recognition task, which runs in a worker thread.
 * 
 * This class implements speech recognition for this demo application. It takes
 * the form of a long-running task which accepts requests to start and stop
 * listening, and emits recognition results to a listener.
 * 
 * @author David Huggins-Daines <dhuggins@cs.cmu.edu>
 */
public class RecognizerTask implements Runnable {
	/**
	 * Audio recording task.
	 * 
	 * This class implements a task which pulls blocks of audio from the system
	 * audio input and places them on a queue.
	 * 
	 * @author David Huggins-Daines <dhuggins@cs.cmu.edu>
	 */
	class AudioTask implements Runnable {
		/**
		 * Queue on which audio blocks are placed.
		 */
		LinkedBlockingQueue<short[]> q;
		AudioRecord rec;
		int block_size;
		boolean done;

		static final int DEFAULT_BLOCK_SIZE = 512;

		AudioTask() {
			this.init(new LinkedBlockingQueue<short[]>(), DEFAULT_BLOCK_SIZE);
		}

		AudioTask(LinkedBlockingQueue<short[]> q) {
			this.init(q, DEFAULT_BLOCK_SIZE);
		}

		AudioTask(LinkedBlockingQueue<short[]> q, int block_size) {
			this.init(q, block_size);
		}

		void init(LinkedBlockingQueue<short[]> q, int block_size) {
			this.done = false;
			this.q = q;
			this.block_size = block_size;
			this.rec = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, 8192);
		}

		public int getBlockSize() {
			return block_size;
		}

		public void setBlockSize(int block_size) {
			this.block_size = block_size;
		}

		public LinkedBlockingQueue<short[]> getQueue() {
			return q;
		}

		public void stop() {
			this.done = true;
		}

		public void run() {
			this.rec.startRecording();
			while (!this.done) {
				int nshorts = this.readBlock();
				if (nshorts <= 0)
					break;
			}
			this.rec.stop();
			this.rec.release();
		}

		int readBlock() {
			short[] buf = new short[this.block_size];
			int nshorts = this.rec.read(buf, 0, buf.length);
			if (nshorts > 0) {
				Log.d(getClass().getName(), "Posting " + nshorts + " samples to queue");
				this.q.add(buf);
			}
			return nshorts;
		}
	}

	/**
	 * PocketSphinx native decoder object.
	 */
	Decoder ps;
	/**
	 * Audio recording task.
	 */
	AudioTask audio;
	/**
	 * Thread associated with recording task.
	 */
	Thread audio_thread;
	/**
	 * Queue of audio buffers.
	 */
	LinkedBlockingQueue<short[]> audioq;
	/**
	 * Listener for recognition results.
	 */
	RecognitionListener rl;
	/**
	 * Whether to report partial results.
	 */
	boolean use_partials;

	/**
	 * State of the main loop.
	 */
	enum State {
		IDLE, LISTENING
	};
	/**
	 * Events for main loop.
	 */
	enum Event {
		NONE, START, STOP, SHUTDOWN
	};
	
	/**
	 * Passed to constructor, to indicate the type of match to check for.
	 *
	 */
	public static enum Mode {PHONEME, SYLLABLE, WORD};

	/**
	 * Current event.
	 */
	Event mailbox;

	public RecognitionListener getRecognitionListener() {
		return rl;
	}

	public void setRecognitionListener(RecognitionListener rl) {
		this.rl = rl;
	}

	public void setUsePartials(boolean use_partials) {
		this.use_partials = use_partials;
	}

	public boolean getUsePartials() {
		return this.use_partials;
	}


	/**
	 * A creator with context, so we can get properly stored data files. 
	 * Uses normal (non-phoneme) form by default.
	 * 
	 * @param context
	 */
	public RecognizerTask(Context context) {
		this(context, Mode.WORD);
	}
	/**
	 * A creator with context, so we can get properly stored data files. 
	 * If usePhonemeMode is true, it operates in a special phoneme-only mode.
	 * 
	 * @param context
	 * @param mode
	 */
	public RecognizerTask(Context context, Mode mode) {
		
		// Copy data files to storage
		String base_dir = context.getFilesDir().getAbsolutePath();
		pocketsphinx.setLogfile(Environment.getExternalStorageDirectory().getPath() + "/pocketsphinx.log");
		Config c = new Config();
		
		c.setString("-hmm", base_dir + "/hmm");
		c.setString("-fdict", base_dir + "/lm/filler.dict");
		switch (mode) {
		case PHONEME:
//			c.setString("-mode", "allphone");	// Not supported in this version, by may be useful in the future
			
//			c.setString("-dict", base_dir + "/lm/phone.dict");
			c.setString("-lm", base_dir + "/lm/interp_nodx.arpa.dmp");	// Better suited to phonemes
			c.setString("-dict", base_dir + "/lm/hub4.5000.dic");
			break;
		case SYLLABLE:
			c.setString("-dict", base_dir + "/lm/hub4.5000.dic");
			c.setString("-lm", base_dir + "/lm/hub4.5000.dmp");
			break;
		default:	// catches WORD
			c.setString("-dict", base_dir + "/lm/hub4.5000.dic");
			c.setString("-lm", base_dir + "/lm/hub4.5000.dmp");
		}
		
		c.setString("-rawlogdir", base_dir);
		c.setFloat("-samprate", 8000.0);
		c.setInt("-maxhmmpf", 10000);
		c.setBoolean("-backtrace", true);
		c.setBoolean("-bestpath", false);
		this.ps = new Decoder(c);
		this.audio = null;
		this.audioq = new LinkedBlockingQueue<short[]>();
		this.use_partials = false;
		this.mailbox = Event.NONE;
	}
	
	

	public void run() {
		/* Main loop for this thread. */
		boolean done = false;
		/* State of the main loop. */
		State state = State.IDLE;
		/* Previous partial hypothesis. */
		String partial_hyp = null;
		
		while (!done) {
			/* Read the mail. */
			Event todo = Event.NONE;
			synchronized (this.mailbox) {
				todo = this.mailbox;
				/* If we're idle then wait for something to happen. */
				if (state == State.IDLE && todo == Event.NONE) {
					try {
						Log.d(getClass().getName(), "waiting");
						this.mailbox.wait();
						//this.mailbox.wait(5000);	//JAM limit wait time
						todo = this.mailbox;
						Log.d(getClass().getName(), "got" + todo);
					} catch (InterruptedException e) {
						/* Quit main loop. */
						Log.e(getClass().getName(), "Interrupted waiting for mailbox, shutting down");
						todo = Event.SHUTDOWN;
					}
				}
				/* Reset the mailbox before releasing, to avoid race condition. */
				this.mailbox = Event.NONE;
			}
			/* Do whatever the mail says to do. */
			switch (todo) {
			case NONE:
				if (state == State.IDLE)
					Log.e(getClass().getName(), "Received NONE in mailbox when IDLE, threading error?");
					//done = true;	//JAM shutdown if time limit reached with no input
				break;
			case START:
				if (state == State.IDLE) { 
					Log.d(getClass().getName(), "START");
					this.audio = new AudioTask(this.audioq, 1024);
					this.audio_thread = new Thread(this.audio);
					this.ps.startUtt();
					this.audio_thread.start();
					state = State.LISTENING;
				}
				else
					Log.e(getClass().getName(), "Received START in mailbox when LISTENING");
				break;
			case STOP:
				if (state == State.IDLE)
					Log.e(getClass().getName(), "Received STOP in mailbox when IDLE");
				else {
					Log.d(getClass().getName(), "STOP");
					assert this.audio != null;
					this.audio.stop();
					try {
						this.audio_thread.join();
					}
					catch (InterruptedException e) {
						Log.e(getClass().getName(), "Interrupted waiting for audio thread, shutting down");
						done = true;
					}
					/* Drain the audio queue. */
					short[] buf;
					while ((buf = this.audioq.poll()) != null) {
						Log.d(getClass().getName(), "Reading " + buf.length + " samples from queue");
						this.ps.processRaw(buf, buf.length, false, false);
					}
					this.ps.endUtt();
					this.audio = null;
					this.audio_thread = null;
					Hypothesis hyp = this.ps.getHyp();
					if (this.rl != null) {
						if (hyp == null) {
							Log.d(getClass().getName(), "Recognition failure");
							this.rl.onError(-1);
						}
						else {
							Bundle b = new Bundle();
							Log.d(getClass().getName(), "Final hypothesis: " + hyp.getHypstr());
							b.putString("hyp", hyp.getHypstr());
							this.rl.onResults(b);
						}
					}
					state = State.IDLE;
				}
				break;
			case SHUTDOWN:
				Log.d(getClass().getName(), "SHUTDOWN");
				if (this.audio != null) {
					this.audio.stop();
					assert this.audio_thread != null;
					try {
						this.audio_thread.join();
					}
					catch (InterruptedException e) {
						/* We don't care! */
					}
				}
				this.ps.endUtt();
				this.audio = null;
				this.audio_thread = null;
				state = State.IDLE;
				done = true;
				break;
			}
			/* Do whatever's appropriate for the current state.  Actually this just means processing audio if possible. */
			if (state == State.LISTENING) {
				assert this.audio != null;
				try {
					short[] buf = this.audioq.take();
					Log.d(getClass().getName(), "Reading " + buf.length + " samples from queue");
					this.ps.processRaw(buf, buf.length, false, false);
					Hypothesis hyp = this.ps.getHyp();
					if (hyp != null) {
						String hypstr = hyp.getHypstr();
						if (hypstr != partial_hyp) {
							Log.d(getClass().getName(), "Hypothesis: " + hyp.getHypstr());
							if (this.rl != null && hyp != null) {
								Bundle b = new Bundle();
								b.putString("hyp", hyp.getHypstr());
								this.rl.onPartialResults(b);
							}
						}
						partial_hyp = hypstr;
					}
				} catch (InterruptedException e) {
					Log.d(getClass().getName(), "Interrupted in audioq.take");
				}
			}
		}
	}

	public void start() {
		Log.d(getClass().getName(), "signalling START");
		synchronized (this.mailbox) {
			this.mailbox.notifyAll();
			Log.d(getClass().getName(), "signalled START");
			this.mailbox = Event.START;
		}
	}

	public void stop() {
		Log.d(getClass().getName(), "signalling STOP");
		synchronized (this.mailbox) {
			this.mailbox.notifyAll();
			Log.d(getClass().getName(), "signalled STOP");
			this.mailbox = Event.STOP;
		}
	}

	public void shutdown() {
		Log.d(getClass().getName(), "signalling SHUTDOWN");
		synchronized (this.mailbox) {
			this.mailbox.notifyAll();
			Log.d(getClass().getName(), "signalled SHUTDOWN");
			this.mailbox = Event.SHUTDOWN;
		}
	}
}