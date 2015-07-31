package com.drink.srv.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdEncoder {
	private final static Logger logger = LoggerFactory.getLogger(IdEncoder.class);
	private static final int LEN = 100000;
	private static long[] shorts = null;
	private static long[] shorts_backword = null;
	static {
		try {
			IdEncoder.init("10w.hash.txt");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void init(String filename) throws Exception {
		InputStream is = null;
		String sys_dir = System.getProperty("user.home") + "/conf/";
		File hashFile = new File(sys_dir + filename);
		if (hashFile.exists()) {
			is = new FileInputStream(hashFile);
			logger.info("load hash file: " + hashFile.getPath());
		} else {
			RuntimeException e = new RuntimeException("hash file load failed! use default");
			if (logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			} else {
				e.printStackTrace();
			}
			is = IdEncoder.class.getClassLoader().getResourceAsStream(filename);
		}

		BufferedReader sr = new BufferedReader(new InputStreamReader(is));
		String line;
		int count = 0, s;
		shorts_backword = new long[LEN];
		shorts = new long[LEN];
		for (int i = 0; i < shorts_backword.length; i++)
			shorts_backword[i] = -1;
		while (true) {
			line = sr.readLine();
			if (StringUtils.isBlank(line))
				break;
			s = Integer.parseInt(line);
			if (shorts_backword[s] != -1)
				throw new Exception("dup:" + s);
			shorts_backword[s] = count;
			shorts[count] = s;
			count++;
		}
		sr.close();
		if (count != LEN)
			throw new Exception("count=" + count);
	}

	public static long EncodeId(long i) {
		long high = i / LEN, low = shorts[(int) (i % LEN)];

		return LEN * shorts[(int) ((high + low) % LEN)] + low;
	}

	public static long DecodeId(long i) {
		long high = i / LEN, low = i % LEN;

		return LEN * ((shorts_backword[(int) high] + LEN - low) % LEN) + shorts_backword[(int) low];
	}

	public static void main(String[] args) {
		try {
			/*
			 * int MAX = 100000; Integer[] data = new Integer[MAX]; for (int i =
			 * 0; i < MAX; ) { data[i] = i++; } Random rand = new Random(); for
			 * (int i = 0; i < MAX; i++) { int r = rand.nextInt(MAX); int v =
			 * data[r]; data[r] = data[i]; data[i] = v; }
			 * 
			 * List<Integer> ll = Arrays.asList(data); File file = new
			 * File("D:\\hash.txt"); file.createNewFile();
			 * FileUtils.writeLines(file, ll);
			 */

			/*
			 * for(int i=0;i<10000000;i++){ long l = IdEncoder.EncodeId(i * 1L);
			 * System.out.println(l); }
			 */
			// IdEncoder.Init("10w.hash.txt");
			long l = IdEncoder.EncodeId(50008799L);
			System.out.println(l);
			System.out.println(IdEncoder.DecodeId(l));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
