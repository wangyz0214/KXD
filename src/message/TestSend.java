package message;

import net.sf.json.JSONObject;

import com.ai.metaq.service.AIMetaQProducer;

public class TestSend {

	public static void main(String[] args){
		JSONObject ployInfo = new JSONObject();
		ployInfo = JSONObject.fromObject("{'__OP_FLAG__':'new','relTag':{'__OP_FLAG__':'new','tagId':'110013'},'subscribeEffWay':{'__OP_FLAG__':'new','featureValue':'1','featureName':'订购生效方式'},'subscribeExpWay':{'__OP_FLAG__':'new','featureValue':'7','featureName':'订购失效方式'},'__TEMPLATE_ID__':3002,'effDate':'2017-04-11 00:00:00','expDate':'2017-04-15 00:00:00','subscribeExpWaySpec':{'__OP_FLAG__':'new','featureName':'订购失效参数','featureValue':'20170415000000'},'relOpers':[{'__OP_FLAG__':'new','operId':'191000000004'}],'relCatalogs':[{'catalogId':'950500000015','__OP_FLAG__':'new'},{'catalogId':'950641000106','__OP_FLAG__':'new'},{'catalogId':'950641000205','__OP_FLAG__':'new'},{'catalogId':'950641000303','__OP_FLAG__':'new'},{'catalogId':'950641001001','__OP_FLAG__':'new'},{'catalogId':'950600000045','__OP_FLAG__':'new'},{'catalogId':'250000000001','__OP_FLAG__':'new'},{'catalogId':'400000000000','__OP_FLAG__':'new'}],'preLimit':[{'__OP_FLAG__':'new','limitType':'TP0613(201613)'},{'__OP_FLAG__':'new','limitType':'TP4123(12002)'},{'__OP_FLAG__':'new','limitType':'TP0042(1019)'}],'frendAllOffer':{'__OP_FLAG__':'new','offerId':'0'},'relRoles':{'__OP_FLAG__':'new','roleName':'手机主角色','roleId':'181000000001'},'beforeHolidayOrderMsg':{'__OP_FLAG__':'new','templateContent':'上海移动提醒服务：已为您成功订购踏青假日流量包，功能费10元，含2017年4月2日0时至2017年4月5日0时间有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量），有效期内方可查询到假日流量包资源及使用情况。开通前已产生的流量按生效前资费标准计算，用尽或到期后按当前流量资费计费，订购关系在假期结束后自动失效。【中国移动】'},'onHolidayOrderMsg':{'__OP_FLAG__':'new','templateContent':'上海移动提醒服务：已为您成功订购踏青假日流量包，功能费10元，含2017年4月5日0时前有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量）。开通前已产生的流量按生效前资费标准计算，用尽或到期后按当前流量资费计费，订购关系在假期结束后自动失效。【中国移动】'},'priceProd':{'__OP_FLAG__':'new','bossFeature':{'__OP_FLAG__':'new','featureValue':'1'},'priceType':{'__OP_FLAG__':'new','featureValue':'2'},'prodBoss1':{'__OP_FLAG__':'new','featureId':'50090'},'prodBoss2':{'__OP_FLAG__':'new','featureId':'50091'},'prodBoss3':{'__OP_FLAG__':'new','paramType':'2'},'bossFeature_featureValue':'1','priceType_featureValue':'2','extendAttrB':'假日流量包1','servicePrice_extendName':'假日流量包流量','servicePrice_extendId':'81505179','price_featureValue':'1000','servicePrice':{'__OP_FLAG__':'new','extendName':'假日流量包流量','extendId':'81505179'},'price':{'__OP_FLAG__':'new','featureValue':'1000'}},'comfirMes':{'__OP_FLAG__':'new','templateContent':'业务介绍：踏青假日流量包，功能费10元，含2017年4月2日0时至2017年4月5日0时间有效的国内可使用流量2GB，不含台港澳及国际漫游流量。<br/>1、开通前已产生的流量按生效前资费标准计算，用尽或到期后按当前流量资费计费。不可重复订购，订购关系在假期结束后自动失效。<br/>2、假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量），有效期内方可查询到假日流量包资源及使用情况。'},'dependKindConf':[{'relKindId':'350700000075','relKindName':'GPRS功能','rowDataId':'0','__OP_FLAG__':'new','relId':'0'}],'offerBusiReport':{'__OP_FLAG__':'new','rptType':'8'},'relRegions':[{'regionId':'1','regionName':'RBOSS营业厅','saleStartTime':'','saleEndTime':'','rowDataId':'0','__OP_FLAG__':'new','relId':'0'},{'regionId':'2','regionName':'网上营业厅','saleStartTime':'','saleEndTime':'','rowDataId':'1','__OP_FLAG__':'new','relId':'0'},{'regionId':'3','regionName':'客服10086','saleStartTime':'','saleEndTime':'','rowDataId':'2','__OP_FLAG__':'new','relId':'0'},{'regionId':'7','regionName':'SMS','saleStartTime':'','saleEndTime':'','rowDataId':'3','__OP_FLAG__':'new','relId':'0'},{'regionId':'11','regionName':'专营店','saleStartTime':'','saleEndTime':'','rowDataId':'4','__OP_FLAG__':'new','relId':'0'},{'regionId':'87','regionName':'互联网外链','saleStartTime':'','saleEndTime':'','rowDataId':'5','__OP_FLAG__':'new','relId':'0'},{'regionId':'86','regionName':'微信营业厅','saleStartTime':'','saleEndTime':'','rowDataId':'6','__OP_FLAG__':'new','relId':'0'},{'regionId':'18','regionName':'自助终端系统','saleStartTime':'','saleEndTime':'','rowDataId':'7','__OP_FLAG__':'new','relId':'0'}],'name':'假日流量包1','mutexKindConf':[],'freeResProd':[{'__OP_FLAG__':'new','__TEMPLATE_ID__':'3009','bossFeature':{'__OP_FLAG__':'new','featureValue':'1'},'effType':{'__OP_FLAG__':'new','featureValue':'7'},'expType':{'__OP_FLAG__':'new','featureValue':'7'},'extendAttrB':'2G国内通用流量','servicePrice':{'__OP_FLAG__':'new','extendName':'2G国内通用流量','extendId':'82700040'},'effTypeParam':{'__OP_FLAG__':'new','featureValue':'20170411000000'},'expTypeParam':{'__OP_FLAG__':'new','featureValue':'20170414000000'},'rowDataId':'0','relId':'0'}],'relPromotional':[],'outerWEBOfferConfig':[{'__OP_FLAG__':'new','effDate':'2017-04-11 00:00:00','expDate':'2017-04-15 00:00:00','remarks':'测试网厅','param3':'节前二次确认语句测试测试','exeParam':{'__OP_FLAG__':'new','featureValue':'节中二次确认语句测试'},'param4':'流量包描述<br/>换行<br/>测试','__TEMPLATE_ID__':'3016','rowDataId':'0','relId':'0'}],'outerSMSOfferConfig':[{'__OP_FLAG__':'new','param3':'上海移动提醒服务：即将为您开通假日流量包，功能费10元，含2017年4月30日0时至2017年5月3日0时间有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量）。用尽或到期后，按当前流量资费计费。请在24小时内回复“是”确认订购，功能费立即收取。','exeParam':{'__OP_FLAG__':'new','featureValue':'上海移动提醒服务：即将为您开通假日流量包，立即生效，功能费10元，含生效起至2017年5月3日0时前有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付流量、统付、日套餐、小时套餐）。用尽或到期后，按当前流量资费计费。请在24小时内回复“是”确认订购，功能费立即收取。'},'__TEMPLATE_ID__':'3008','effDate':'2017-04-11 00:00:00','expDate':'2017-04-15 00:00:00','remarks':'短厅测试','param2':'ktjrtc','param4':'测试非订购时间回复语<br/>测试','rowDataId':'0','relId':'0'}],'outerSSTOfferConfig':[{'__OP_FLAG__':'new','param4':'2017年3月20日-4月4日期间订购假日流量包，享买1G送1G优惠！<br/>即开通业务后，您可合计获得2GB国内假日流量（包括原假日流量包内的1GB流量和额外赠送的1GB流量），流量使用时间为4月2日0点-4月4日24点（清明小长假期间）。<br/><br/>出门踏青扫墓，再也不用为流量不够用发愁啦！<br/>','param3':'业务规则:<br/>（一）开放对象：上海移动三大品牌有上网功能的2G/3G/4G客户，无限量套餐客户除外。<br/>（二）流量使用范围：包含的流量不区分CMWAP和CMNET，只适用于个人客户在国内通过GPRS、EDGE、TD-SCDMA、HSDPA等移动通信技术上网所产生的 数据流量，但不适用于国际/港澳台漫游、 Blackberry、M2M等集团客户、行业应用以及通过WLAN、CSD等其他方式上网产生的流量。<br/>（三）生效/失效规则：节假日前订购，则订购成功后，于距订购日最近的一个节假日生效，有效期自假日第一天0时起至假日最后一天24时，订购 关系在假期结束后自动失效；节假日期间订 购，则订购成功后，有效期自订购之日起至假日最后一天24时。 <br/>（四）申请互斥规则：一般在节假日前的一周内开放订购，在节假日结束后暂停订购；有效期内不可再次订购假日流量包。（五）流量计费规则：假日流量包订购成功后收取全部费用，不回退不取消。假日流量仅从生效起开始使用，不对之前发生的超量流量进行追溯； 假日生效期间，如有定向或统付流量将优先使用，之后抵扣假日包流量，随后再抵扣可选包或加油包等流量；用尽 或到期后，按当前流量资费计费。<br/>','__TEMPLATE_ID__':'3015','effDate':'2017-04-11 00:00:00','expDate':'2017-04-15 00:00:00','remarks':'自助终端测试','rowDataId':'0','relId':'0'}],'relLimitGrp':[],'outerH5OfferConfig':[{'__OP_FLAG__':'new','effDate':'2017-04-11 00:00:00','expDate':'2017-04-15 00:00:00','remarks':'微厅测试','param3':'节前二次确认语句测试','exeParam':{'__OP_FLAG__':'new','featureValue':'节中二次确认语句测试'},'param4':'流量包描述<br/>测试换行<br/>','__TEMPLATE_ID__':'3016','rowDataId':'0','relId':'0'}],'userCreditOne':'TP0323(3000325)','userCreditTwo':'TP0323(3000326)','userCreditThree':'TP0323(3000327)','userCreditFour':'TP0323(3000328)','userCreditFive':'TP0323(3000329)','userCreditFiveGold':'TP0323(3000330)','userCreditFiveDiamond':'TP0323(3000331)','isCheckBalanceFeature':{'__OP_FLAG__':'new','featureValue':'1'},'eBusinessCode':{'__OP_FLAG__':'new','componentId':'999020050000001'},'isCheckBalancePreLimit':{'__OP_FLAG__':'new','limitType':'TP0616(201616)'},'__OBJECT_ID__':31000,'preLimitObject':{'__OP_FLAG__':'new','limitType':'TP0323(3000325) or TP0323(3000326) or TP0323(3000327) or TP0323(3000328) or TP0323(3000329) or TP0323(3000330) or TP0323(3000331)','expDate':'2017-04-11 12:53:18'},'offerId':111000306482,'alphaName':'JRLLB1','userCreditNull':'0','userCreditPre':'0','itemType':'OFFER_VAS','offerPlanType':'2','offerType':'OFFER_VAS','prodSpecId':'171000000001','busiType':'3002','UITemplateId':3002}");
//		ployInfo = JSONObject.fromObject("{'__OP_FLAG__':'new','relTag':{'__OP_FLAG__':'new111111111','tagId':'110013'},'subscribeEffWay':{'__OP_FLAG__':'new','featureValue':'1','featureName':'订购生效方式'},'subscribeExpWay':{'__OP_FLAG__':'new','featureValue':'7','featureName':'订购失效方式'},'__TEMPLATE_ID__':3002,'effDate':'2017-04-01 00:00:00','expDate':'2017-05-06 00:00:00','subscribeExpWaySpec':{'__OP_FLAG__':'new','featureName':'订购失效参数','featureValue':'20170406000000'},'relOpers':[{'__OP_FLAG__':'new','operId':'191000000004'},{'__OP_FLAG__':'new','operId':'191001002001'}],'relCatalogs':[{'catalogId':'950500000015','__OP_FLAG__':'new'},{'catalogId':'950641000106','__OP_FLAG__':'new'},{'catalogId':'950641000205','__OP_FLAG__':'new'},{'catalogId':'950641000303','__OP_FLAG__':'new'},{'catalogId':'950641001001','__OP_FLAG__':'new'},{'catalogId':'950600000045','__OP_FLAG__':'new'},{'catalogId':'250000000001','__OP_FLAG__':'new'},{'catalogId':'400000000000','__OP_FLAG__':'new'}],'preLimit':[{'__OP_FLAG__':'new','limitType':'TP0613(201613)'},{'__OP_FLAG__':'new','limitType':'TP4123(12002)'},{'__OP_FLAG__':'new','limitType':'TP0042(1019)'}],'frendAllOffer':{'__OP_FLAG__':'new','offerId':'0'},'relRoles':{'__OP_FLAG__':'new','roleName':'手机主角色','roleId':'181000000001'},'beforeHolidayOrderMsg':{'__OP_FLAG__':'new','templateContent':'上海移动提醒服务：已为您成功订购踏青假日流量包，功能费10元，含2017年4月2日0时至2017年4月5日0时间有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量），有效期内方可查询到假日流量包资源及使用情况。开通前已产生的流量按生效前资费标准计算，用尽或到期后按当前流量资费计费，订购关系在假期结束后自动失效。【中国移动】'},'onHolidayOrderMsg':{'__OP_FLAG__':'new','templateContent':'上海移动提醒服务：已为您成功订购踏青假日流量包，功能费10元，含2017年4月5日0时前有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量）。开通前已产生的流量按生效前资费标准计算，用尽或到期后按当前流量资费计费，订购关系在假期结束后自动失效。【中国移动】'},'priceProd':{'__OP_FLAG__':'new','bossFeature':{'__OP_FLAG__':'new','featureValue':'1'},'priceType':{'__OP_FLAG__':'new','featureValue':'2'},'prodBoss1':{'__OP_FLAG__':'new','featureId':'50090'},'prodBoss2':{'__OP_FLAG__':'new','featureId':'50091'},'bossFeature_featureValue':'1','priceType_featureValue':'2','extendAttrB':'假日流量包测试0401(资费)','servicePrice_extendName':'假日流量包流量','servicePrice_extendId':'81505179','price_featureValue':'900','servicePrice':{'__OP_FLAG__':'new','extendName':'假日流量包流量','extendId':'81505179'},'price':{'__OP_FLAG__':'new','featureValue':'900'}},'comfirMes':{'__OP_FLAG__':'new','templateContent':'业务介绍：踏青假日流量包，功能费10元，含2017年4月2日0时至2017年4月5日0时间有效的国内可使用流量2GB，不含台港澳及国际漫游流量。<br/>1、开通前已产生的流量按生效前资费标准计算，用尽或到期后按当前流量资费计费。不可重复订购，订购关系在假期结束后自动失效。<br/>2、假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量），有效期内方可查询到假日流量包资源及使用情况。'},'dependKindConf':[{'relKindId':'350700000075','relKindName':'GPRS功能','rowDataId':'0','__OP_FLAG__':'new','relId':'0'}],'name':'假日流量包测试0401','freeResProds':[{'__OP_FLAG__':'new','__TEMPLATE_ID__':'3009','bossFeature':{'__OP_FLAG__':'new','featureValue':'1'},'effType':{'__OP_FLAG__':'new','featureValue':'7'},'expType':{'__OP_FLAG__':'new','featureValue':'7'},'extendAttrB':'免费资源1','servicePrice':{'__OP_FLAG__':'new','extendName':'流量包330','extendId':'82700057'},'effTypeParam':{'__OP_FLAG__':'new','featureValue':'20170403000000'},'expTypeParam':{'__OP_FLAG__':'new','featureValue':'20170405000000'},'rowDataId':'0','relId':'0'}],'mutexKindConf':[],'relRegions':[{'regionId':'0','regionName':'全部渠道','saleStartTime':'','saleEndTime':'','rowDataId':'0','__OP_FLAG__':'new','relId':'0'}],'relPromotional':[],'outerSMSOfferConfig':[{'__OP_FLAG__':'new','param3':'上海移动提醒服务：即将为您开通假日流量包，功能费10元，含2017年4月30日0时至2017年5月3日0时间有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付、日套餐、小时套餐流量）。用尽或到期后，按当前流量资费计费。请在24小时内回复“是确认订购，功能费立即收取。','exeParam':{'__OP_FLAG__':'new','featureValue':'上海移动提醒服务：即将为您开通假日流量包，立即生效，功能费10元，含生效起至2017年5月3日0时前有效的2GB国内流量（不含港澳台），假日生效期间优先使用（仅次于定向、统付流量、统付、日套餐、小时套餐）。用尽或到期后，按当前流量资费计费。请在24小时内回复“是”确认订购，功能费立即收取。'},'__TEMPLATE_ID__':'3008','effDate':'2017-04-01 00:00:00','expDate':'2017-04-05 00:00:00','remarks':'测试','param2':'ktjrtc','param4':'测试','rowDataId':'0','relId':'0'}],'outerWEBOfferConfig':[{'__OP_FLAG__':'new','effDate':'2017-04-03 00:00:00','expDate':'2017-04-05 00:00:00','remarks':'测试','param3':'节前二次确认语句测试','exeParam':{'__OP_FLAG__':'new','featureValue':'节中二次确认语句测试'},'param4':'描述语句测试','__TEMPLATE_ID__':'3016','rowDataId':'0','relId':'0'}],'outerH5OfferConfig':[{'__OP_FLAG__':'new','effDate':'2017-04-01 00:00:00','expDate':'2017-04-05 00:00:00','remarks':'测试','param3':'节前确认语句测试','exeParam':{'__OP_FLAG__':'new','featureValue':'节中确认语句测试'},'param4':'描述语句测试','__TEMPLATE_ID__':'3016','rowDataId':'0','relId':'0'}],'outerSSTOfferConfig':[{'__OP_FLAG__':'new','effDate':'2017-04-01 00:00:00','expDate':'2017-04-05 00:00:00','remarks':'测试','param3':'二次确认语句测试','param4':'描述测试','__TEMPLATE_ID__':'3006','rowDataId':'0','relId':'0'}],'relLimitGrp':[],'userCreditOne':'TP0323(3000325)','userCreditTwo':'TP0323(3000326)','userCreditThree':'TP0323(3000327)','userCreditFour':'TP0323(3000328)','userCreditFive':'TP0323(3000329)','userCreditFiveGold':'TP0323(3000330)','userCreditFiveDiamond':'TP0323(3000331)','__OBJECT_ID__':31000,'preLimitObject':{'__OP_FLAG__':'new','limitType':'TP0323(3000325) or TP0323(3000326) or TP0323(3000327) or TP0323(3000328) or TP0323(3000329) or TP0323(3000330) or TP0323(3000331)','expDate':'2017-04-01 13:06:46'},'offerId':111000304288,'alphaName':'JRLLBCS0401','userCreditNull':'0','userCreditPre':'0','itemType':'OFFER_VAS','offerPlanType':'2','offerType':'OFFER_VAS','prodSpecId':'171000000001','busiType':'3002'}");
		boolean flag;
		try {
			//flag = AIMetaQProducer.sendMessage("upc4Sms",GlobalCfg.getProperty("upc.msg.zookeeper.url"), ployInfo.toString());
			System.out.println("---" + SocketIsok.getProperty());
			flag = AIMetaQProducer.sendMessage("upc4Sst",SocketIsok.getProperty(), ployInfo.toString());
			System.out.println(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
