package com.drink.web.controller;

import com.drink.common.web.filter.AbstractFirstFilter;

public class BaseController {
	protected final static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJGnaxT2GDlshZuNQyiYpU6geZ2rv04XkKyWFoQlD8Hi25O8rIxuKF7CVpQwITBotj29yJrIevzzYeiz5s0PtY/FM4keQXYZS/SxINDm0g8ixNVJqk4pK8tBjqvHNFALUMAVn4bhkXzS5xKEJTmv7cXX8gISSR7ksg1hU6YO9nAtAgMBAAECgYBHKUeM5yswqw+fzuV36RQilQ619Nozehnp6C46A61uKbrjtDONLKi2mzYXkmg0sYQQKC/hR3+nI/W67mId38XxKkjWlL+1GgiuekAzp18RJt0OhztgxomlflTbAGA97lhifGB88JFCQVLvFomKVFbhOoZhi7VPwRmdXHjG9RGs9QJBAMV/uzmxZFeSM1+8gOszx2/kfJVlespIHn3xMBSo6SqEvsrYMjzZ1b8pI0wpfMNPc24G5sEWVaL0U0UJKGPMCZcCQQC8zE2EL4kvFBLMUDyzpbILTVvP4JCsyKJ5css7fDt/ybhB41wFyUJ1z2illdLVDgThayZuC+Cn1mmjOcsalyTbAkEAqIL5zpoLEaUO7Iq8PPLrIg+ENTxm7p1mVvMpoRcWC09EM3MQOldrRzXkhiGH8GWkCacd0HYsD2QEgIz1x2DadQJBAIjph5VCe7N+VRuBXGPS10jG8rvPWFtUrC5yhts+fk9vU4XEgSlZCC8zF+psuhXzMqrd72KCjopoPPc3pAhGb10CQA+ZIb/RJcdiBUGtfXi2p0+VJtOUDH043P9vgoPsFEob8Zbki+xnrmU1W8qnfQo8qUlqvW5K2G3szHTe5NmrIqg=";
	protected final static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCRp2sU9hg5bIWbjUMomKVOoHmdq79OF5CslhaEJQ/B4tuTvKyMbihewlaUMCEwaLY9vciayHr882Hos+bND7WPxTOJHkF2GUv0sSDQ5tIPIsTVSapOKSvLQY6rxzRQC1DAFZ+G4ZF80ucShCU5r+3F1/ICEkke5LINYVOmDvZwLQIDAQAB";

	public final String getRSAPublicKey() {
		return PUBLIC_KEY;
	}

	public final String getRSAPrivateKey() {
		return PRIVATE_KEY;
	}

	protected final static class Constants {
		public final static String CONS_PHONE = "phone";
		public final static String CONS_PASSWORD = "password";
		public final static String CONS_CAPTCHA = "captcha";
		public final static String CONS_AESKEY = "aeskey";
		public final static String CONS_CITY_ID = "cityId";
		public final static String CONS_COUNTRY_CODE = "countryCode";
		public final static String CONS_LAT = "lat";
		public final static String CONS_LNG = "lng";

		public final static String CONS_TOKEN = AbstractFirstFilter.ACCESS_KEY_TOKEN;
		public final static String CONS_SECRET = AbstractFirstFilter.ACCESS_SECRET;
		
		public static final String CONS_SMS_TYPE = "type";
	}
}
