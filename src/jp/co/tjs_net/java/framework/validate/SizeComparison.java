package jp.co.tjs_net.java.framework.validate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jp.co.tjs_net.java.framework.information.IndexInformation;


public class SizeComparison extends DateLimit {
	public SizeComparison(HttpServletRequest req, HttpServletResponse res, IndexInformation info) {
		super(req, res, info);
	}
	
	@Override
	public boolean doValidate(HttpServletRequest req, HttpServletResponse res, String value, IndexInformation info) throws Exception {
		
		// 項目の大小比較を行う。
		// from		比較対象1
		// to		比較対象2
		// include	同値を含めるか。0なら含めない、1なら含める。
		
		String from = this.params.get("from").toString();
		String valueF	= req.getParameter(from);

		String to = this.params.get("to").toString();
		String valueT	= req.getParameter(to);
		
		String include = this.params.get("include").toString();
		
		// 比較条件の前提として
		// 数値であること。
		
		// 日付の場合は区切り文字をとって数値に変換できるか確認をする。
		valueF = valueF.replaceAll("/", "");
		valueF = valueF.replaceAll("-", "");
		valueF = valueF.replaceAll(":", "");
		valueF = valueF.replaceAll(" ", "");
		
		valueT = valueT.replaceAll("/", "");
		valueT = valueT.replaceAll("-", "");
		valueT = valueT.replaceAll(":", "");
		valueT = valueT.replaceAll(" ", "");
		
		// 二つの項目が設定されている場合のみチェックする。
		if ("".equals(valueF) || "".equals(valueT)) {
			return true;
		}
		
		try {
			// 文字列をint型に変換を試みる
			Integer numF = Integer.parseInt(valueF);
			Integer numT = Integer.parseInt(valueT);
			
			if ("1".equals(include)) {
				// 同値を含む。
				if (0 < numF.compareTo(numT)) {
					return false;
				}
			} else {
				// 同値を含めない。
				if (0 <= numF.compareTo(numT)) {
					return false;
				}
			}
			
		} catch (NumberFormatException e) {
			// 変換に失敗した場合はfalseを返す
			return false;
		}
		
		return true;
	}
}
