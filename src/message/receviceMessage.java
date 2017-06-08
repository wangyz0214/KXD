package message;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ai.metaq.service.AIMetaQConsumer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class receviceMessage {

//	private static String topic = "upc4Sst";
//	private static String zkConnect = "10.10.152.31:2181";
//	private static String group = "upc4Sms_config";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
//	 		AIMetaQConsumer.recieveMessages("upc4Sst", "10.10.152.31:2181", "upc4Sms_config", TestMetaQ.class.getName(), false);
	 		
			TestMetaQ();
			
			System.out.println("---------------");

	}

	private static void TestMetaQ() {
		// TODO Auto-generated method stub
		
		JSONObject ployInfo = new JSONObject();
//		ployInfo = JSONObject.fromObject("{'__OP_FLAG__': 'new','__TEMPLATE_ID__': 5800,'expDate': '2017-12-01 00:00:00','relOpers': [{'__OP_FLAG__': 'new','operId': '191000000004'},{'__OP_FLAG__': 'new','operId': '191000000016'}],'relRoles': {'__OP_FLAG__': 'new','roleName': '手机主角色','roleId': '181000000001'},'relRoles_roleName': '手机主角色','relFriendOffers': {'__OP_FLAG__': 'new','offerId': '0'},'relProds': [{'prodId': '390000050347','prodName': '充值送礼活动用户预存','optionType': '0','rowDataId': '0','__OP_FLAG__': 'new','relId': '0'}],'relRegions': [{'regionId': '1','regionName': 'RBOSS营业厅','saleStartTime': '','saleEndTime': '','rowDataId': '5','__OP_FLAG__': 'new','relId': '0'},{'regionId': '2','regionName': '网上营业厅','saleStartTime': '','saleEndTime': '','rowDataId': '6','__OP_FLAG__': 'new','relId': '0'},{'regionId': '18','regionName': '自助终端系统','saleStartTime': '20091231','saleEndTime': '20991231','rowDataId': '12','__OP_FLAG__': 'new','relId': '0'}],   'special': {'__OP_FLAG__': 'new','featureValue': '1001'},   'name': '校园充值送（2017迎新篇）100元档测试','month': {'__OP_FLAG__': 'new','featureValue': '12'},'effDate': '2017-07-01 00:00:00','bootMessage': {'__OP_FLAG__': 'new','templateContent': ''},'comfirMes': {'__OP_FLAG__': 'new','templateContent': ''},'offerReport': {'__OP_FLAG__': 'new','rptType': '3','acctCode': '5505051','extendAttrB': '3520024','catalogId': '200000000021'},'ifIncludeComb': {'__OP_FLAG__': 'new','extendAttrF': '181000000001','relatProductItemIdName': '流量10元可选包（100M）','relatProductItemId': '350353001404','paramValueName': '移动数据10元档以上套餐','paramValue': '380000021335'},'relPromOfferVas': [],'BOUpOuterPloyH5': [],'': [],'outerSMSPloy': [],'relSKU': [],'BOUpOuterPloyComb': [],'prestoreDivideMoney': {'__OP_FLAG__': 'new','promoType': '1','allotAmount': '10000','allotModeValue': '3','prestoreFee': '10000','bookItemIdOut': '5505051','bookItemIdIn': '5024051','allotScaleName': '8|8|8|8|8|8|8|8|8|8|8|12|','allotScale': '800|800|800|800|800|800|800|800|800|800|800|1200|','divideMonth': '12','validDate': '2017-07-01 00:00:00','promoName': '校园充值送（2017迎新篇）100元档测试','allotBusiCode': '202014001','allotSmsType': '1','allotSmsExpr': '881 '},'deposit': {'__OP_FLAG__': 'new','feeAmount': '10000','prodName': '充值送礼活动用户预存100(元)','accCode': '5505051'},'desc': '校园充值送（2017迎新篇）100元档测试','renewOfferKind1': {'__OP_FLAG__': 'new','relKindName': '1','relKindId': '391000039503'},'renewOfferKind2': {'__OP_FLAG__': 'new','relKindId': '391000039503','relKindName': '1'},'punishFeeY': '100','catalogId': {'__OP_FLAG__': 'new','catalogName': '校园充值送（2017迎新篇）','catalogId': '200000000021','busiDesc': '活动内容：客户预存和包并承诺月最低消费及流量可选包(或为18元及以上档次4G套餐)，即可获赠相应金额的和包。赠送和包一次性立即到账，预存和包分12个月返还。每个号码办理1次。','busiRuleDesc': '一、活动规则<br>(一)活动对象：<br>1、本活动仅向全球通、动感地带、神州行三大品牌客户开放（神州行易通卡和初始套装除外）。<br>2、本活动仅向上海移动1-5星客户开放，客户可发送CXXJ到10086查询客户星级。随e行品牌、和多号业务中的副号码、流量无限量套餐客户、新激活不超过6个自然月内的客户不能办理此类活动。<br>3、统付类-家庭畅享计划/和家庭副号码客户，需在主号码授权后方可参加此类活动。<br>4、客户办理此类活动时需状态有效、未停机。后付费客户需先结清所有账单，预付费客户需已激活入网。<br>5、此类活动需做好真实有效实名制登记后方可办理。个人客户凭本人有效真实身份证件原件，经办人凭机主有效身份证件原件和本人有效身份证原件办理；单位客户提供与客户名称一致的企业介绍信（加盖公章），经办人凭本人有效身份证原件，携带有效的企业单位营业执照副本原件办理。<br>6、已办理过充值送礼（含充值送话费、充值送和包电子券、充值送积分等）活动且尚未到期的客户不能办理本活动。<br>(二)办理次数:<br>同一个号码最多可参加1次本活动。<br>(三)预存话费返还与月个人最低消费规则：<br>1、活动办理成功后次月开始生效。预存话费从次月起分月返还，每月5日前到账，活动赠返话费均不能进行通信账户支付。<br>2、若客户通过话费支付方式参加活动，需在办理活动前10天内累计充值达到活动相应预存话费+5元，才可参加活动。客户只有自行预存的话费才可以作为活动预存款，赠返话费均不可作为活动预存款。<br>3、活动设定每月个人最低消费，若协议期内每月未满足当月个人最低消费，则按最低消费金额补足。账单中一级科目“代收费业务费用”不纳入月个人最低消费额度。如参加活动号码是家庭统付主号码，每月承诺的最低消费仅指主号码产生的费用，不包含统付成员费用。<br>4、如参与档次设定每月最低流量可选包档次，客户在协议期间每月使用不低于活动要求的流量可选包档次【18元及以上档次4G资费套餐无需开通】。参与活动时，若客户次月不满足最低流量可选包档次要求的，系统将自动为客户按最低流量可选包档次开通并于活动受理次月生效。协议期间，客户不可转入低档次的流量可选包。<br>5、其他承诺消费类活动的客户与本活动每月个人最低消费金额需叠加。<br>6、参加了家庭营销活动的家庭统付群组中的主、副号码若参加了个人营销活动，除个人需补足其个人营销活动的最低消费外，还需在家庭类活动的最低消费上叠加所有主副号码个人营销活动等额的最低消费对群组进行补足。<br>(四)赠送规则：<br>活动办理成功后，可立即获赠相应的和包电子券。本活动赠送的和包电子券自发放之日起90天内有效，过期不补。和包电子券不可回退、不可转赠、不可转让、不能兑现。<br>(五)其他规则：<br>1、活动成功受理后默认开通手机支付功能（免费）、和生活普通会员（免费）、移动杉德卡（优惠期内免费）。<br>2、此类活动办理后，不可回退，不可变更档次。活动预存话费不可退费，不可资金转移。协议期内，客户不可办理过户、销户、停机保号等业务。<br>3、如办理客户当前品牌具有有效期，则受理后将根据预存金额一次性延长有效期，后续返还话费不再增加有效期。<br>4、如办理客户为家庭统付活动（如畅享计划、和家庭等）的副号码，在家庭统付期间，获赠话费暂无法使用。客户如需使用，可通过“家庭统付副号码话费转赠”业务，每月开账时由系统根据副号码当月实际开账金额，将账户余额中可用于抵扣当月消费的部分结转至主号码账户。<br>5、如参加活动号码是家庭统付主号码，每月承诺的最低消费仅指主号码产生的费用，不包含统付成员费用。<br>'},'relProds4': {'__OP_FLAG__': 'new','prodName': '补足28元','extendName': '补足28元','extendId': '41000043'},'monthFee': {'__OP_FLAG__': 'new','featureValue': '2800'},'directFee': {'__OP_FLAG__': 'new','feeAmount': '10000','prodName': '承诺消费系列活动抵扣预存100(元)'},'__OBJECT_ID__': 20000,'preFeeRuleComb': {'__OP_FLAG__': 'new','paramValue': 100,'effDate': '2017-04-01','expDate': '2099-12-31'},'offerId': 111000307276,'alphaName': 'XYCZS（2017YXP）100YDCS','prestoreExtendId': 12060,'noPrepayBack': '0','type': 'OFFER_PLOY','trademark': '0','offerPlanType': '5','offerType': 'OFFER_PLOY','prodSpecId': '171000000001','isGlobal': '0','smsTamplateChoose': '1'}");
		ployInfo = JSONObject.fromObject("{'__OP_FLAG__': 'new','prodSpecId': '171000000001','prodSpecName': 'GSM产品规格','busiType': '1','relRegions': [{'regionId': '0','rowDataId': '0','__OP_FLAG__': 'new','relId': '0'}],'relPlanGroup': { '__OP_FLAG__': 'new','tagName': '后付费4G流量卡','tagId': '130006'},'name': '后付费流量至尊套餐288元档','offerType': 'OFFER_PLAN','payType': '0','tradeMark': '161000000012','offerPlanType': '1','effDate': '2018-01-01 00:00:00','expDate': '2099-12-31 00:00:00','relSameNameProd': {'__OP_FLAG__': 'new','isMain': '0','extendIdName': '后付费流量至尊套餐288元档','extendId': '82700004','upProdItemObject': {'__OP_FLAG__': 'new','type': 'SRVC_SINGLE'},'upPlanProdRel': {'__OP_FLAG__': 'new','extendF': '181000000001'},'relService': {'__OP_FLAG__': 'new','offerGroupId': '270000050001','relType': 'SRVC_SINGLE_PRICE_SERVICE'},'upItemFeature': {'__OP_FLAG__': 'new','featureId': '10000017','featureValue': '1'}},'BossType': {'__OP_FLAG__': 'new','featureValue': '1'},'preOpenLever': {'__OP_FLAG__': 'new','featureValue': '1'},'relRoles': {'__OP_FLAG__': 'new','roleName': '手机主角色','roleId': '181000000001','isMainRole': '1'},'desc': '后付费流量至尊套餐288元档介绍','pointPlan': {'__OP_FLAG__': 'new','featureValue': '1'},'__TEMPLATE_ID__': 666,'relCatalogInfo': [{'catalogId': '250000000003','catalogName': '基本套餐','rowDataId': '1','__OP_FLAG__': 'new','relId': '0'},{'catalogId': '950641000308','catalogName': '基本策划','baseCatalogFlag': '策划销售目录','rowDataId': '2','__OP_FLAG__': 'new','relId': '0'},{'catalogId': '950500000012','catalogName': '个人套餐','rowDataId': '3','__OP_FLAG__': 'new','relId': '0'},{'catalogId': '950641000110','catalogName': '4G套餐','baseCatalogFlag': '策划销售目录','rowDataId': '4','__OP_FLAG__': 'new','relId': '0'},{'catalogId': '950641000111','catalogName': '4G套餐','baseCatalogFlag': '策划销售目录','rowDataId': '5','__OP_FLAG__': 'new','relId': '0'},{'catalogId': '950641000112','catalogName': '4G套餐','baseCatalogFlag': '策划销售目录','rowDataId': '6','__OP_FLAG__': 'new','relId': '0'},{'catalogId': '950641000113','catalogName': '4G套餐','baseCatalogFlag': '策划销售目录','rowDataId': '7','__OP_FLAG__': 'new','relId': '0'}],'refOfferTemp': {'__OP_FLAG__': 'new','isRefOffer': '1','refOfferId': '390000020081','refCondition': '191000000003'},'refOfferName': '后付费4G飞享套餐58元档','entityId': 111000303477,'bootMessage': {'__OP_FLAG__': 'new','templateContent': '入'},'relProds': [{'prodId': '310014001001','prodName': '移动IP17951电话','optionType': '0','rowDataId': '0','__OP_FLAG__': 'new','relId': '0'},{'prodId': '310090106001','prodName': '呼叫等待','optionType': '2','rowDataId': '1','__OP_FLAG__': 'new','relId': '0'},{'prodId': '310090112001','prodName': '国内呼叫转移','optionType': '1','rowDataId': '2','__OP_FLAG__': 'new','relId': '0'},{'prodId': '310090113001','prodName': '国内长话','optionType': '0','rowDataId': '3','__OP_FLAG__': 'new','relId': '0'},{'prodId': '310090116001','prodName': '国内漫游','optionType': '0','rowDataId': '4','__OP_FLAG__': 'new','relId': '0'}],'relOpers': [],'relOffers': [{'offerType': '增值策划','prodSpecName': 'GSM产品规格','relOfferId': '350315001001','relOfferName': 'WLAN标准资费','relType': 'OFFER_PLAN_INCLUDE_OFFER_PLAN','includeType': '1','autoDiscountProdId': '','autoDiscountProdIdName': '','rowDataId': '0','__OP_FLAG__': 'new','relId': '0'},{'offerType': '增值策划','prodSpecName': 'GSM产品规格','relOfferId': '350350005001','relOfferName': '点对点短信息','relType': 'OFFER_PLAN_INCLUDE_OFFER_PLAN','includeType': '1','autoDiscountProdId': '','autoDiscountProdIdName': '','rowDataId': '1','__OP_FLAG__': 'new','relId': '0'},{'offerType': '增值策划','prodSpecName': 'GSM产品规格','relOfferId': '350353001001','relOfferName': '移动数据标准资费','relType': 'OFFER_PLAN_INCLUDE_OFFER_PLAN','includeType': '1','autoDiscountProdId': '','autoDiscountProdIdName': '','rowDataId': '2','__OP_FLAG__': 'new','relId': '0'},{'offerType': '增值策划','prodSpecName': 'GSM产品规格','relOfferId': '350390103001','relOfferName': '主叫显示','relType': 'OFFER_PLAN_INCLUDE_OFFER_PLAN','includeType': '1','autoDiscountProdId': '','autoDiscountProdIdName': '','rowDataId': '3','__OP_FLAG__': 'new','relId': '0'},{'offerType': '增值策划','prodSpecName': 'GSM产品规格','relOfferId': '350390208001','relOfferName': '直拨长途一费制','relType': 'OFFER_PLAN_INCLUDE_OFFER_PLAN','includeType': '0','autoDiscountProdId': '','autoDiscountProdIdName': '','rowDataId': '4','__OP_FLAG__': 'new','relId': '0'},{'offerType': '增值策划','prodSpecName': 'GSM产品规格','relOfferId': '390000000092','relOfferName': '移动数据资费（0.29元/M,满60元1G）','relType': 'OFFER_PLAN_INCLUDE_OFFER_PLAN','includeType': '0','autoDiscountProdId': '','autoDiscountProdIdName': '','rowDataId': '5','__OP_FLAG__': 'new','relId': '0'}],'preStoreLevel': [],'relSKU': [],'relChgOffers': [],'relChgOffersIn': [],'relKinds': [],'upOuterPloy': [],'relNewRpt': {'__OP_FLAG__': 'new','rptType': '6','extendAttrA': '卡类'},'privilege': {'__OP_FLAG__': 'new','privId': '90000003'},'extendName': '后付费流量至尊套餐288元档','extendId': '22000003','__OBJECT_ID__': 10000,'offerId': 111000303477,'alphaName': 'HFFLLZZTC288YD','type': 'OFFER_PLAN'}");
//		
		
		String offerId = null;
		String buss_name = null;
		String main_business_id = null;
		//资费
		String saleStartTime = null; //上架时间
		String saleEndTime = null; //下架时间
		String tagName = null;	//大类名称
		String tagId = null;	//大类分组
		String bussdesp = null; //业务介绍
		
		offerId = ployInfo.get("offerId").toString();
		buss_name = ployInfo.get("name").toString();
		
		bussdesp = ployInfo.get("desc").toString();
		
		if(ployInfo.containsKey("relPlanGroup")){ //大类分组
			JSONObject relPlanGroup = ployInfo.getJSONObject("relPlanGroup");
			tagName = relPlanGroup.get("tagName").toString();
			tagId = relPlanGroup.get("tagId").toString();
			if("130001".equals(tagId)){			//预付费4G飞享套餐
				main_business_id = "165";
			}else if("130002".equals(tagId)){	//后付费4G飞享套餐
				main_business_id = "166";
			}else if("130003".equals(tagId)){	//预付费4G无线上网套餐
				main_business_id = "173";
			}else if("130004".equals(tagId)){	//后付费4G无线上网套餐
				main_business_id = "174";
			}else if("130005".equals(tagId)){	//预付费4G流量卡
				main_business_id = "178";
			}else if("130006".equals(tagId)){	//后付费4G流量卡
				main_business_id = "179";
			}
		}
		
		if(ployInfo.containsKey("relRegions")){ //渠道信息，上下架时间
			JSONArray relRegions = ployInfo.getJSONArray("relRegions");
			for(int i = 0; i<relRegions.size(); i++){
				JSONObject term = relRegions.getJSONObject(i);
				if("18".equals(term.get("regionId"))){ //18-自助终端
					saleStartTime = term.get("saleStartTime").toString();
					saleEndTime = term.get("saleEndTime").toString();
				}
			}
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss"); 
		String ctime = formatter.format(new Date());  //当前时间
		System.out.println("当前时间 ---- " + ctime);
		
		System.out.println("业务ID = " + offerId);
		System.out.println("业务名称 = " + buss_name);
		System.out.println("上架时间 = " + saleStartTime);
		System.out.println("下架时间 = " + saleEndTime);
		System.out.println("业务描述 = " + bussdesp);
		System.out.println("大类名称 = " + tagName);
		System.out.println("大类分组 = " + tagId);
		System.out.println("父类ID = " + main_business_id);
		
		
		
	}

}
