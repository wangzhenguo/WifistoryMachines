package com.chomp.wifistorymachine.ui.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Message;
import android.util.Log;



public class Mp3Recorder {
	
	private static final String TAG = Mp3Recorder.class.getSimpleName();
	
	static {
		System.loadLibrary("mp3lame");
	}

	private static final int DEFAULT_SAMPLING_RATE = 44100;
	
	private static final int FRAME_COUNT = 160;
	
	/* Encoded bit rate. MP3 file will be encoded with bit rate 32kbps */ 
	private static final int BIT_RATE = 128;

	private AudioRecord audioRecord = null;

	private int bufferSize;

	private File mp3File;
	
	private RingBuffer ringBuffer;
	
	private byte[] buffer;

	private FileOutputStream os = null;

	private DataEncodeThread encodeThread;

	private int samplingRate;

	private int channelConfig;

	private PCMFormat audioFormat;
	
	private boolean isRecording = false;


	private String mDir;             // 文件夹的名称
	private String mCurrentFilePath;

	private static Mp3Recorder mInstance;





	public Mp3Recorder(int samplingRate, int channelConfig,
			PCMFormat audioFormat) {
		this.samplingRate = samplingRate;
		this.channelConfig = channelConfig;
		this.audioFormat = audioFormat;
	}

	/**
	 * Default constructor. Setup recorder with default sampling rate 1 channel,
	 * 16 bits pcm
	 */
	public Mp3Recorder(String dir) {
		this(DEFAULT_SAMPLING_RATE, AudioFormat.ENCODING_PCM_16BIT,
				PCMFormat.PCM_16BIT);
		mDir = dir;
	}
	/**
	 * 回调“准备完毕”
	 * @author songshi
	 *
	 */
	public interface AudioStateListenter {
		void wellPrepared();    // prepared完毕
	}

	public AudioStateListenter mListenter;

	public void setOnAudioStateListenter(AudioStateListenter audioStateListenter) {
		mListenter = audioStateListenter;
	}

	/**
	 * 使用单例实现 AudioManage
	 * @param dir
	 * @return
	 */
	//DialogManage主要管理Dialog，Dialog主要依赖Context，而且此Context必须是Activity的Context，
	//如果DialogManage写成单例实现，将是Application级别的，将无法释放，容易造成内存泄露，甚至导致错误
	public static Mp3Recorder getInstance(String dir) {
		if (mInstance == null) {
			synchronized (Mp3Recorder.class) {   // 同步
				if (mInstance == null) {
					mInstance = new Mp3Recorder(dir);
				}
			}
		}
		return mInstance;
	}

	/**
	 * Start recording. Create an encoding thread. Start record from this
	 * thread.
	 * 
	 * @throws IOException
	 */
	double amplitudeDb;
	public void startRecording() throws IOException {
		if (isRecording) return;
		Log.d(TAG, "Start recording");
		Log.d(TAG, "BufferSize = " + bufferSize);
		// Initialize audioRecord if it's null.
		if (audioRecord == null) {
			initAudioRecorder();
		}
		audioRecord.startRecording();

		if (mListenter != null) {
			mListenter.wellPrepared();
		}
		
		new Thread() {

			@Override
			public void run() {
				isRecording = true;
				while (isRecording) {
					int bytes = audioRecord.read(buffer, 0, bufferSize);
					int amplitude = (buffer[0] & 0xff) << 8 | buffer[1];
					 amplitudeDb = 20 * Math.log10((double)Math.abs(amplitude) / 32768);
					if (bytes > 0) {
						ringBuffer.write(buffer, bytes);
					}
				}
				
				// release and finalize audioRecord
				try {
					audioRecord.stop();
					audioRecord.release();
					audioRecord = null;

					// stop the encoding thread and try to wait
					// until the thread finishes its job
					Message msg = Message.obtain(encodeThread.getHandler(),
							DataEncodeThread.PROCESS_STOP);
					msg.sendToTarget();
					
					Log.d(TAG, "waiting for encoding thread");
					encodeThread.join();
					Log.d(TAG, "done encoding thread");


				} catch (InterruptedException e) {
					Log.d(TAG, "Faile to join encode thread");
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}			
		}.start();
	}

	/**
	 * 释放资源
	 * @throws IOException
	 */
	public void release() throws IOException {
		Log.d(TAG, "stop recording");
		isRecording = false;



	}



	/**
	 * 取消（释放资源+删除文件）
	 */
	public void cancel() {
        try {
			release();
		}catch (Exception e){

		}
		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			file.delete();    //删除录音文件
			mCurrentFilePath = null;
		}
	}



	/**
	 * Initialize audio recorder
	 */
	private void initAudioRecorder() throws IOException {

		int bytesPerFrame = audioFormat.getBytesPerFrame();
		/* Get number of samples. Calculate the buffer size (round up to the
		   factor of given frame size) */
		int frameSize = AudioRecord.getMinBufferSize(samplingRate,
				channelConfig, audioFormat.getAudioFormat()) / bytesPerFrame;
		if (frameSize % FRAME_COUNT != 0) {
			frameSize = frameSize + (FRAME_COUNT - frameSize % FRAME_COUNT);
			Log.d(TAG, "Frame size: " + frameSize);
		}
		
		bufferSize = frameSize * bytesPerFrame;

		/* Setup audio recorder */
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				samplingRate, channelConfig, audioFormat.getAudioFormat(),
				bufferSize);

		
		// Setup RingBuffer. Currently is 10 times size of hardware buffer
		// Initialize buffer to hold data
		ringBuffer = new RingBuffer(10 * bufferSize);
		buffer = new byte[bufferSize];
		
		// Initialize lame buffer
		// mp3 sampling rate is the same as the recorded pcm sampling rate 
		// The bit rate is 32kbps
		SimpleLame.init(samplingRate, 1, samplingRate, BIT_RATE);
		
		// Initialize the place to put mp3 file

		File directory = new File(mDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		String fileName = GenerateFileName(); // 文件名字
		File mp3File = new File(directory, fileName);  // 路径+文件名字
		mCurrentFilePath = mp3File.getAbsolutePath();


		os = new FileOutputStream(mp3File);

		// Create and run thread used to encode data
		// The thread will 
		encodeThread = new DataEncodeThread(ringBuffer, os, bufferSize);
		encodeThread.start();
		audioRecord.setRecordPositionUpdateListener(encodeThread, encodeThread.getHandler());
		audioRecord.setPositionNotificationPeriod(FRAME_COUNT);


	}

	/**
	 * 随机生成文件名称
	 * @return
	 */
	private String GenerateFileName() {
		// TODO Auto-generated method stub
		return "app_"+UUID.randomUUID().toString() + ".amr"; // 音频文件格式
	}

	public String getCurrentFilePath() {
		// TODO Auto-generated method stub
		return mCurrentFilePath;
	}

	/**
	 * 获得音量等级——通过mMediaRecorder获得振幅，然后换算成声音Level
	 * maxLevel最大为7；
	 * mMediaRecorder.getMaxAmplitude() / 32768：0——1；
	 * maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1： 1——7；
	 * @param maxLevel
	 * @return
	 */
	public int getVoiceLevel(int maxLevel) {
		int i=0;
//		if (isPrepared) {
			try {
				//mMediaRecorder.getMaxAmplitude()——获得最大振幅:1-32767

			//	return maxLevel * MediaRecorder.getMaxAmplitude() / 32768 + 1;//getMaxAmplitude()
				if(i<=3){
					i=0;
				}
				i++;
				return i;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
//		}
		return 1;
	}

}