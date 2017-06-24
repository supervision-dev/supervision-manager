package com.rmbank.supervision.common.utils;

import java.lang.reflect.Array;

/**
 * md5 缁鐤勯悳棰佺啊RSA Data Security, Inc.閸︺劍褰佹禍銈囩舶IETF  
 * 閻ㄥ嚧FC1321娑擃厾娈慚D5 message-digest 缁犳纭堕妴? 
 * @author AllenZhang
 *
 */
public class MD5Util {
	/*
	 * 娑撳娼版潻娆庣昂S11-S44鐎圭偤妾稉濠冩Ц娑�閲�4閻ㄥ嫮鐓╅梼纰夌礉閸︺劌甯慨瀣畱C鐎圭偟骞囨稉顓熸Ц閻�define 鐎圭偟骞囬惃鍕剁礉 鏉╂瑩鍣烽幎濠傜暊娴狀剙鐤勯悳鐗堝灇娑撶皧tatic
	 * final閺勵垵銆冪粈杞扮啊閸欘亣顕伴敍灞藉瀼閼宠棄婀崥灞肩娑擃亣绻樼粙瀣敄闂傛潙鍞撮惃鍕樋娑�Instance闂傛潙鍙℃禍?
	 */
	static final int S11 = 7;
	static final int S12 = 12;
	static final int S13 = 17;
	static final int S14 = 22;

	static final int S21 = 5;
	static final int S22 = 9;
	static final int S23 = 14;
	static final int S24 = 20;

	static final int S31 = 4;
	static final int S32 = 11;
	static final int S33 = 16;
	static final int S34 = 23;

	static final int S41 = 6;
	static final int S42 = 10;
	static final int S43 = 15;
	static final int S44 = 21;

	static final byte[] PADDING = { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };
	/*
	 * 娑撳娼伴惃鍕瑏娑擃亝鍨氶崨妯绘ЦMD5鐠侊紕鐣绘潻鍥┾柤娑擃厾鏁ら崚鎵畱3娑擃亝鐗宠箛鍐╂殶閹诡噯绱濋崷銊ュ斧婵娈慍鐎圭偟骞囨稉?鐞氼偄鐣炬稊澶婂煂MD5_CTX缂佹挻鐎稉?
	 */
	private long[] state = new long[4]; // state (ABCD)
	private long[] count = new long[2]; // number of bits, modulo 2^64 (lsb
										// first)
	private byte[] buffer = new byte[64]; // input buffer

	/*
	 * digestHexStr閺勭枹D5閻ㄥ嫬鏁稉?绔存稉顏勫彆閸忚鲸鍨氶崨姗堢礉閺勵垱娓堕弬棰佺濞喡ゎ吀缁犳绮ㄩ弸婊呮畱 閵�16鏉╂稑鍩桝SCII鐞涖劎銇�
	 */
	public String digestHexStr;

	/*
	 * digest,閺勵垱娓堕弬棰佺濞喡ゎ吀缁犳绮ㄩ弸婊呮畱2鏉╂稑鍩楅崘鍛村劥鐞涖劎銇氶敍宀冦�缁�28bit閻ㄥ嚜D5閸�
	 */
	private byte[] digest = new byte[16];

	/*
	 * getMD5ofStr閺勵垳琚玀D5閺�瀵岀憰浣烘畱閸忣剙鍙￠弬瑙勭《閿涘苯鍙嗛崣锝呭棘閺佺増妲告担鐘冲厒鐟曚浇绻樼悰瀛5閸欐ɑ宕查惃鍕摟缁楋缚瑕�
	 * 鏉╂柨娲栭惃鍕Ц閸欐ɑ宕茬�宀�畱缂佹挻鐏夐敍宀冪箹娑擃亞绮ㄩ弸婊勬Ц娴犲骸鍙曢崗杈ㄥ灇閸涙igestHexStr閸欐牕绶遍惃鍕剁礋
	 */
	public String getMD5ofStr(String inbuf) {
		md5Init();
		md5Update(inbuf.getBytes(), inbuf.length());
		md5Final();
		digestHexStr = "";
		for (int i = 0; i < 16; i++) {
			digestHexStr += byteHEX(digest[i]);
		}
		return digestHexStr;

	}

	// 鏉╂瑦妲窶D5鏉╂瑤閲滅猾鑽ゆ畱閺嶅洤鍣弸鍕�閸戣姤鏆熼敍瀛瀉vaBean鐟曚焦鐪伴張澶夌娑撶尩ublic閻ㄥ嫬鑻熸稉鏃�梾閺堝寮弫鎵畱閺嬪嫰?閸戣姤鏆�
	public MD5Util() {
		md5Init();
	}

	/* md5Init閺勵垯绔存稉顏勫灥婵瀵查崙鑺ユ殶閿涘苯鍨垫慨瀣閺嶇绺鹃崣姗�櫤閿涘矁顥婇崗銉︾垼閸戝棛娈戦獮缁樻殶 */
	private void md5Init() {
		count[0] = 0L;
		count[1] = 0L;
		// /* Load magic initialization constants.

		state[0] = 0x67452301L;
		state[1] = 0xefcdab89L;
		state[2] = 0x98badcfeL;
		state[3] = 0x10325476L;

		return;
	}

	/*
	 * F, G, H ,I 閺�娑擃亜鐔�張顒傛畱MD5閸戣姤鏆熼敍灞芥躬閸樼喎顫愰惃鍑狣5閻ㄥ嚋鐎圭偟骞囨稉顓ㄧ礉閻㈠彉绨�鍐ф粦閺�
	 * 缁�宕熼惃鍕秴鏉╂劗鐣婚敍灞藉讲閼宠棄鍤禍搴㈡櫏閻滃洨娈戦懓鍐閹跺﹤鐣犳禒顒�杽閻滅増鍨氭禍鍡楃暞閿涘苯婀猨ava娑擃叏绱濋幋鎴滄粦閹跺﹤鐣犳禒?閵�?鐎圭偟骞囬幋鎰啊private閺傝纭堕敍灞芥倳鐎涙ぞ绻氶幐浣风啊閸樼喐娼礐娑擃厾娈戦妴?
	 */

	private long F(long x, long y, long z) {
		return (x & y) | ((~x) & z);

	}

	private long G(long x, long y, long z) {
		return (x & z) | (y & (~z));

	}

	private long H(long x, long y, long z) {
		return x ^ y ^ z;
	}

	private long I(long x, long y, long z) {
		return y ^ (x | (~z));
	}

	/*
	 * FF,GG,HH閸滃瓥I鐏忓棜鐨熼悽鈥�G,H,I鏉╂稖顢戞潻鎴滅濮濄儱褰夐幑?FF, GG, HH, and II transformations for
	 * rounds 1, 2, 3, and 4. Rotation is separate from addition to prevent
	 * recomputation.
	 */

	private long FF(long a, long b, long c, long d, long x, long s, long ac) {
		a += F(b, c, d) + x + ac;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		a += b;
		return a;
	}

	private long GG(long a, long b, long c, long d, long x, long s, long ac) {
		a += G(b, c, d) + x + ac;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		a += b;
		return a;
	}

	private long HH(long a, long b, long c, long d, long x, long s, long ac) {
		a += H(b, c, d) + x + ac;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		a += b;
		return a;
	}

	private long II(long a, long b, long c, long d, long x, long s, long ac) {
		a += I(b, c, d) + x + ac;
		a = ((int) a << s) | ((int) a >>> (32 - s));
		a += b;
		return a;
	}

	/*
	 * md5Update閺勭枹D5閻ㄥ嫪瀵岀拋锛勭暬鏉╁洨鈻奸敍瀹╪buf閺勵垵顩﹂崣妯诲床閻ㄥ嫬鐡ч懞鍌欒閿涘nputlen閺勵垶鏆辨惔锔肩礉鏉╂瑤閲�
	 * 閸戣姤鏆熼悽鐪奺tMD5ofStr鐠嬪啰鏁ら敍宀冪殶閻€劋绠ｉ崜宥夋付鐟曚浇鐨熼悽鈺ヾ5init閿涘苯娲滃銈嗗Ω鐎瑰啳顔曠拋鈩冨灇private閻�
	 */
	private void md5Update(byte[] inbuf, int inputLen) {

		int i, index, partLen;
		byte[] block = new byte[64];
		index = (int) (count[0] >>> 3) & 0x3F;
		// /* Update number of bits */
		if ((count[0] += (inputLen << 3)) < (inputLen << 3))
			count[1]++;
		count[1] += (inputLen >>> 29);

		partLen = 64 - index;

		// Transform as many times as possible.
		if (inputLen >= partLen) {
			md5Memcpy(buffer, inbuf, index, 0, partLen);
			md5Transform(buffer);

			for (i = partLen; i + 63 < inputLen; i += 64) {

				md5Memcpy(block, inbuf, 0, i, 64);
				md5Transform(block);
			}
			index = 0;

		} else

			i = 0;

		// /* Buffer remaining input */
		md5Memcpy(buffer, inbuf, index, i, inputLen - i);

	}

	/*
	 * md5Final閺佸鎮婇崪灞斤綖閸愭瑨绶崙铏圭波閺�
	 */
	private void md5Final() {
		byte[] bits = new byte[8];
		int index, padLen;

		// /* Save number of bits */
		Encode(bits, count, 8);

		// /* Pad out to 56 mod 64.
		index = (int) (count[0] >>> 3) & 0x3f;
		padLen = (index < 56) ? (56 - index) : (120 - index);
		md5Update(PADDING, padLen);

		// /* Append length (before padding) */
		md5Update(bits, 8);

		// /* Store state in digest */
		Encode(digest, state, 16);

	}

	/*
	 * md5Memcpy閺勵垯绔存稉顏勫敶闁劋濞囬悽銊ф畱byte閺佹壆绮嶉惃鍕健閹风柉绀夐崙鑺ユ殶閿涘奔绮爄nput閻ㄥ埇npos瀵�顫愰幎濡塭n闂�灝瀹抽惃?閵�?閵�?閵�
	 * 鐎涙濡幏鐤閸掔殎utput閻ㄥ埣utpos娴ｅ秶鐤嗗?顫�
	 */

	private void md5Memcpy(byte[] output, byte[] input, int outpos, int inpos,
			int len) {
		int i;

		for (i = 0; i < len; i++)
			output[outpos + i] = input[inpos + i];
	}

	/*
	 * md5Transform閺勭枹D5閺嶇绺鹃崣妯诲床缁嬪绨敍灞炬箒md5Update鐠嬪啰鏁ら敍瀹恖ock閺勵垰鍨庨崸妤冩畱閸樼喎顫愮�妤勫Ν
	 */
	private void md5Transform(byte block[]) {
		long a = state[0], b = state[1], c = state[2], d = state[3];
		long[] x = new long[16];

		Decode(x, block, 64);

		/* Round 1 */
		a = FF(a, b, c, d, x[0], S11, 0xd76aa478L); /* 1 */
		d = FF(d, a, b, c, x[1], S12, 0xe8c7b756L); /* 2 */
		c = FF(c, d, a, b, x[2], S13, 0x242070dbL); /* 3 */
		b = FF(b, c, d, a, x[3], S14, 0xc1bdceeeL); /* 4 */
		a = FF(a, b, c, d, x[4], S11, 0xf57c0fafL); /* 5 */
		d = FF(d, a, b, c, x[5], S12, 0x4787c62aL); /* 6 */
		c = FF(c, d, a, b, x[6], S13, 0xa8304613L); /* 7 */
		b = FF(b, c, d, a, x[7], S14, 0xfd469501L); /* 8 */
		a = FF(a, b, c, d, x[8], S11, 0x698098d8L); /* 9 */
		d = FF(d, a, b, c, x[9], S12, 0x8b44f7afL); /* 10 */
		c = FF(c, d, a, b, x[10], S13, 0xffff5bb1L); /* 11 */
		b = FF(b, c, d, a, x[11], S14, 0x895cd7beL); /* 12 */
		a = FF(a, b, c, d, x[12], S11, 0x6b901122L); /* 13 */
		d = FF(d, a, b, c, x[13], S12, 0xfd987193L); /* 14 */
		c = FF(c, d, a, b, x[14], S13, 0xa679438eL); /* 15 */
		b = FF(b, c, d, a, x[15], S14, 0x49b40821L); /* 16 */

		/* Round 2 */
		a = GG(a, b, c, d, x[1], S21, 0xf61e2562L); /* 17 */
		d = GG(d, a, b, c, x[6], S22, 0xc040b340L); /* 18 */
		c = GG(c, d, a, b, x[11], S23, 0x265e5a51L); /* 19 */
		b = GG(b, c, d, a, x[0], S24, 0xe9b6c7aaL); /* 20 */
		a = GG(a, b, c, d, x[5], S21, 0xd62f105dL); /* 21 */
		d = GG(d, a, b, c, x[10], S22, 0x2441453L); /* 22 */
		c = GG(c, d, a, b, x[15], S23, 0xd8a1e681L); /* 23 */
		b = GG(b, c, d, a, x[4], S24, 0xe7d3fbc8L); /* 24 */
		a = GG(a, b, c, d, x[9], S21, 0x21e1cde6L); /* 25 */
		d = GG(d, a, b, c, x[14], S22, 0xc33707d6L); /* 26 */
		c = GG(c, d, a, b, x[3], S23, 0xf4d50d87L); /* 27 */
		b = GG(b, c, d, a, x[8], S24, 0x455a14edL); /* 28 */
		a = GG(a, b, c, d, x[13], S21, 0xa9e3e905L); /* 29 */
		d = GG(d, a, b, c, x[2], S22, 0xfcefa3f8L); /* 30 */
		c = GG(c, d, a, b, x[7], S23, 0x676f02d9L); /* 31 */
		b = GG(b, c, d, a, x[12], S24, 0x8d2a4c8aL); /* 32 */

		/* Round 3 */
		a = HH(a, b, c, d, x[5], S31, 0xfffa3942L); /* 33 */
		d = HH(d, a, b, c, x[8], S32, 0x8771f681L); /* 34 */
		c = HH(c, d, a, b, x[11], S33, 0x6d9d6122L); /* 35 */
		b = HH(b, c, d, a, x[14], S34, 0xfde5380cL); /* 36 */
		a = HH(a, b, c, d, x[1], S31, 0xa4beea44L); /* 37 */
		d = HH(d, a, b, c, x[4], S32, 0x4bdecfa9L); /* 38 */
		c = HH(c, d, a, b, x[7], S33, 0xf6bb4b60L); /* 39 */
		b = HH(b, c, d, a, x[10], S34, 0xbebfbc70L); /* 40 */
		a = HH(a, b, c, d, x[13], S31, 0x289b7ec6L); /* 41 */
		d = HH(d, a, b, c, x[0], S32, 0xeaa127faL); /* 42 */
		c = HH(c, d, a, b, x[3], S33, 0xd4ef3085L); /* 43 */
		b = HH(b, c, d, a, x[6], S34, 0x4881d05L); /* 44 */
		a = HH(a, b, c, d, x[9], S31, 0xd9d4d039L); /* 45 */
		d = HH(d, a, b, c, x[12], S32, 0xe6db99e5L); /* 46 */
		c = HH(c, d, a, b, x[15], S33, 0x1fa27cf8L); /* 47 */
		b = HH(b, c, d, a, x[2], S34, 0xc4ac5665L); /* 48 */

		/* Round 4 */
		a = II(a, b, c, d, x[0], S41, 0xf4292244L); /* 49 */
		d = II(d, a, b, c, x[7], S42, 0x432aff97L); /* 50 */
		c = II(c, d, a, b, x[14], S43, 0xab9423a7L); /* 51 */
		b = II(b, c, d, a, x[5], S44, 0xfc93a039L); /* 52 */
		a = II(a, b, c, d, x[12], S41, 0x655b59c3L); /* 53 */
		d = II(d, a, b, c, x[3], S42, 0x8f0ccc92L); /* 54 */
		c = II(c, d, a, b, x[10], S43, 0xffeff47dL); /* 55 */
		b = II(b, c, d, a, x[1], S44, 0x85845dd1L); /* 56 */
		a = II(a, b, c, d, x[8], S41, 0x6fa87e4fL); /* 57 */
		d = II(d, a, b, c, x[15], S42, 0xfe2ce6e0L); /* 58 */
		c = II(c, d, a, b, x[6], S43, 0xa3014314L); /* 59 */
		b = II(b, c, d, a, x[13], S44, 0x4e0811a1L); /* 60 */
		a = II(a, b, c, d, x[4], S41, 0xf7537e82L); /* 61 */
		d = II(d, a, b, c, x[11], S42, 0xbd3af235L); /* 62 */
		c = II(c, d, a, b, x[2], S43, 0x2ad7d2bbL); /* 63 */
		b = II(b, c, d, a, x[9], S44, 0xeb86d391L); /* 64 */

		state[0] += a;
		state[1] += b;
		state[2] += c;
		state[3] += d;

	}

	/*
	 * Encode閹跺ong閺佹壆绮嶉幐澶愩�鎼村繑濯堕幋鎭噛te閺佹壆绮嶉敍灞芥礈娑撶皜ava閻ㄥ埐ong缁鐎烽弰?4bit閻ㄥ嫸绱�閸欘亝濯舵担?2bit閿涘奔浜掗柅鍌氱安閸樼喎顫怌鐎圭偟骞囬惃鍕暏闁�
	 */
	private void Encode(byte[] output, long[] input, int len) {
		int i, j;

		for (i = 0, j = 0; j < len; i++, j += 4) {
			output[j] = (byte) (input[i] & 0xffL);
			output[j + 1] = (byte) ((input[i] >>> 8) & 0xffL);
			output[j + 2] = (byte) ((input[i] >>> 16) & 0xffL);
			output[j + 3] = (byte) ((input[i] >>> 24) & 0xffL);
		}
	}

	/*
	 * Decode閹跺ゲyte閺佹壆绮嶉幐澶愩�鎼村繐鎮庨幋鎰灇long閺佹壆绮嶉敍灞芥礈娑撶皜ava閻ㄥ埐ong缁鐎烽弰?4bit閻ㄥ嫸绱�
	 * 閸欘亜鎮庨幋鎰秵32bit閿涘矂鐝�2bit濞撳懘娴傞敍灞间簰闁倸绨查崢鐔奉潗C鐎圭偟骞囬惃鍕暏闁�
	 */
	private void Decode(long[] output, byte[] input, int len) {
		int i, j;

		for (i = 0, j = 0; j < len; i++, j += 4)
			output[i] = b2iu(input[j]) | (b2iu(input[j + 1]) << 8)
					| (b2iu(input[j + 2]) << 16) | (b2iu(input[j + 3]) << 24);

		return;
	}

	/*
	 * b2iu閺勵垱鍨滈崘娆戞畱娑�閲滈幎濂瞴te閹稿鍙庢稉宥�閾忔垶顒滅拹鐔峰娇閻ㄥ嫬甯崚娆戞畱閿涘倸宕屾担宥忕磽缁嬪绨敍灞芥礈娑撶皜ava濞屸剝婀乽nsigned鏉╂劗鐣�
	 */
	public static long b2iu(byte b) {
		return b < 0 ? b & 0x7F + 128 : b;
	}

	/*
	 * byteHEX()閿涘瞼鏁ら弶銉﹀Ω娑�閲渂yte缁鐎烽惃鍕殶鏉烆剚宕查幋鎰磩閸忣叀绻橀崚鍓佹畱ASCII鐞涖劎銇氶敍?
	 * 閵�娲滄稉绨�va娑擃厾娈慴yte閻ㄥ墖oString閺冪姵纭剁�鐐靛箛鏉╂瑤绔撮悙鐧哥礉閹存垳婊戦崣鍫熺梾閺堝鐠囶叀鈻堟稉顓犳畱 sprintf(outbuf,"%02X",ib)
	 */
	public static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static void main(String args[]) {
		MD5Util m = new MD5Util();
		if (Array.getLength(args) == 0) { // 婵″倹鐏夊▽鈩冩箒閸欏倹鏆熼敍灞惧⒔鐞涘本鐖ｉ崙鍡欐畱Test Suite

			System.out.println("MD5 Test suite:");
			System.out.println("MD5(\"\"):" + m.getMD5ofStr(""));
			System.out.println("MD5(\"a\"):" + m.getMD5ofStr("a"));
			System.out.println("MD5(\"abc\"):" + m.getMD5ofStr("abc"));
			System.out.println("MD5(\"message digest\"):"+ m.getMD5ofStr("message digest"));
			System.out.println("MD5(\"abcdefghijklmnopqrstuvwxyz\"):"+ m.getMD5ofStr("abcdefghijklmnopqrstuvwxyz"));
			System.out.println("MD5(\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\"):"+ m.getMD5ofStr("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"));
		} else
			System.out.println("MD5(" + args[0] + ")=" + m.getMD5ofStr(args[0]));
	}

}
