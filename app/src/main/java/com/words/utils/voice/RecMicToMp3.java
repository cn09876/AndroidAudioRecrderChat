package com.words.utils.voice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import com.uraroji.garage.android.lame.SimpleLame;

public class RecMicToMp3
{
	private final String TAG = "RecMicToMp3";
	
	static {
		System.loadLibrary("mp3lame");
	}

	private String mFilePath;
	private int mSampleRate;
	private boolean mIsRecording = false;



	/**
	 * 
	 * @param filePath
	 * @param sampleRate
	 */
	public RecMicToMp3(String filePath, int sampleRate) {
		if (sampleRate <= 0) {
			throw new InvalidParameterException(
					"Invalid sample rate specified.");
		}
		this.mFilePath = filePath;
		this.mSampleRate = sampleRate;
	}

	public void start() {
		if (mIsRecording) {
			return;
		}

		new Thread()
        {
			@Override
			public void run()
            {
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				final int minBufferSize = AudioRecord.getMinBufferSize(
						mSampleRate, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);

				if (minBufferSize < 0)
                {
					return;
				}
				AudioRecord audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, mSampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

				// PCM buffer size (5sec)
				// SampleRate[Hz] * 16bit * Mono * 5sec
				short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5];
				byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

				FileOutputStream output = null;
				try {
					output = new FileOutputStream(new File(mFilePath));
				} catch (FileNotFoundException e) {
					Log.e(TAG, mFilePath + " file not find");
					return;
				}

				// Lame init
				SimpleLame.init(mSampleRate, 1, mSampleRate, 32);
				mIsRecording = true;
				try {
					try {
						audioRecord.startRecording();
					} catch (IllegalStateException e) {
						return;
					}

					try {
						int readSize = 0;
						while (mIsRecording) {
							readSize = audioRecord.read(buffer, 0, minBufferSize);
							if (readSize < 0)
                            {
								break;
							}
                            else if (readSize == 0)
                            {

							}
                            else
                            {
								int encResult = SimpleLame.encode(buffer, buffer, readSize, mp3buffer);
								if (encResult < 0)
                                {
									break;
								}
								if (encResult != 0) {
									try {
										output.write(mp3buffer, 0, encResult);
									} catch (IOException e) {
										break;
									}
								}
							}
						}

						int flushResult = SimpleLame.flush(mp3buffer);
						if (flushResult < 0)
                        {
						}
						if (flushResult != 0)
                        {
							try
                            {
								output.write(mp3buffer, 0, flushResult);
							}
                            catch (IOException e)
                            {
							}
						}

						try
                        {
							output.close();
						}
                        catch (IOException e)
                        {
						}
					} finally {
						audioRecord.stop();
						audioRecord.release();
					}
				} finally {
					SimpleLame.close();
					mIsRecording = false;
				}
			}
		}.start();
	}
	
	public void stop() {
		mIsRecording = false;
	}

	public void discard() {
		stop();
		// delete the file
		Log.d(TAG, "delete file:" + mFilePath);
		File file = new File(mFilePath);
		file.delete();
	}

	public boolean isRecording() {
		return mIsRecording;
	}

}
