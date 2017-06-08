package kxd.engine.fileservice;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import kxd.net.connection.jndi.Lookuper;
import kxd.net.naming.LoopNamingContext;
import kxd.remote.scs.beans.BaseAdCategory;
import kxd.remote.scs.beans.BaseFileItem;
import kxd.remote.scs.beans.BaseOrg;
import kxd.remote.scs.beans.BaseOrgAd;
import kxd.remote.scs.beans.BaseTerm;
import kxd.remote.scs.beans.BaseTermAd;
import kxd.remote.scs.interfaces.OrgAdBeanRemote;
import kxd.remote.scs.interfaces.TermAdBeanRemote;
import kxd.remote.scs.util.emun.AdPriority;
import kxd.remote.scs.util.emun.AdStoreType;
import kxd.util.DateTime;
import kxd.util.KeyValue;

public class FileUploadManager {
	public void setSavePath(FileUploadProcessor processor,
			HttpServletRequest request, BaseFileItem item) {
		short fileCategoryId = processor.getFileCategory().getId();
		switch (fileCategoryId) {
		case 4:
			item.setSavePath(request.getAttribute("orgid") + "/"
					+ request.getAttribute("adcategory") + "/");
			break;
		case 5:
			item.setSavePath(request.getAttribute("termid") + "/"
					+ request.getAttribute("adcategory") + "/");
			break;
		}
	}

	public void checkFileExists(long loginUserId,
			FileUploadProcessor processor, HttpServletRequest request,
			BaseFileItem item) throws ParseException, NamingException,
			IOException, InterruptedException, NoSuchAlgorithmException {
		short fileCategoryId = processor.getFileCategory().getId();
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			switch (fileCategoryId) {
			case 4: {
				OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
				if (bean.find(Integer.valueOf((String) request
						.getAttribute("id"))) == null) {
					throw new IOException("找不到要编辑的广告文件");
				}
			}
				break;
			case 5: {
				TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-termAdBean", TermAdBeanRemote.class);
				if (bean.find(Integer.valueOf((String) request
						.getAttribute("id"))) == null) {
					throw new IOException("找不到要编辑的广告文件");
				}
			}
				break;
			}
		} finally {
			context.close();
		}
	}

	protected String convertResponse(Object o) {
		if (o instanceof BaseOrgAd) {
			BaseOrgAd ad = (BaseOrgAd) o;
			return "{\"id\":\"" + ad.getIdString() + "\",\"desp\":\""
					+ ad.getAdDesp() + "\",\"category\":\""
					+ ad.getAdCategory().getAdCategoryDesp()
					+ "\",\"filename\":\"" + ad.getFileName()
					+ "\",\"orgdesp\":\"" + ad.getOrg().getOrgName() + "\"}";
		} else if (o instanceof BaseTermAd) {
			BaseTermAd ad = (BaseTermAd) o;
			return "{\"id\":\"" + ad.getIdString() + "\",\"desp\":\""
					+ ad.getAdDesp() + "\",\"category\":\""
					+ ad.getAdCategory().getAdCategoryDesp()
					+ "\",\"filename\":\"" + ad.getFileName()
					+ "\",\"termdesp\":\"" + ad.getTerm().getTermDesp() + "\"}";
		}
		return null;
	}

	public String addOrEdit(long loginUserId, boolean uploadComplete,
			FileUploadProcessor processor, HttpServletRequest request,
			BaseFileItem item) throws ParseException, NamingException,
			IOException, InterruptedException, NoSuchAlgorithmException {
		short fileCategoryId = processor.getFileCategory().getId();
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			switch (fileCategoryId) {
			case 4: {
				OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
				BaseOrgAd orgAd = new BaseOrgAd();
				orgAd.setOrg(new BaseOrg(Integer.valueOf((String) request
						.getAttribute("orgid"))));
				orgAd.setAdCategory(new BaseAdCategory(Short
						.valueOf((String) request.getAttribute("adcategory"))));
				orgAd.setAdDesp((String) request.getAttribute("addesp"));
				orgAd.setStartDate(new DateTime((String) request
						.getAttribute("startdate"), "yyyy-MM-dd").getTime());
				orgAd.setEndDate(new DateTime((String) request
						.getAttribute("enddate") + " 23:59:59",
						"yyyy-MM-dd HH:mm:ss").getTime());
				orgAd.setDuration(Integer.valueOf((String) request
						.getAttribute("duration")));
				orgAd.setUploadComplete(uploadComplete);
				orgAd.setFileName(item.getRealFileName());
				orgAd.setPriority(AdPriority.valueOfIntString(request
						.getAttribute("priority")));
				orgAd.setStoreType(AdStoreType.valueOfIntString(request
						.getAttribute("storetype")));
				orgAd.setUrl(processor.getHttpUrl(orgAd.getFileName()));
				if (request.getAttribute("id") != null) {
					orgAd.setId(Integer.valueOf((String) request
							.getAttribute("id")));
					KeyValue<String, BaseOrgAd> r = bean.edit(loginUserId,
							orgAd);
					processor.deleteFile(loginUserId, r.getKey(), false);
					orgAd = r.getValue();
				} else {
					orgAd = bean.add(loginUserId, orgAd);
					request.setAttribute("id", orgAd.getId().toString());
				}
				return convertResponse(orgAd);
			}
			case 5: {
				TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-termAdBean", TermAdBeanRemote.class);
				BaseTermAd ad = new BaseTermAd();
				ad.setTerm(new BaseTerm(Integer.valueOf((String) request
						.getAttribute("termid"))));
				ad.setAdCategory(new BaseAdCategory(Short
						.valueOf((String) request.getAttribute("adcategory"))));
				ad.setAdDesp((String) request.getAttribute("addesp"));
				ad.setStartDate(new DateTime((String) request
						.getAttribute("startdate"), "yyyy-MM-dd").getTime());
				ad.setEndDate(new DateTime((String) request
						.getAttribute("enddate") + " 23:59:59",
						"yyyy-MM-dd HH:mm:ss").getTime());
				ad.setDuration(Integer.valueOf((String) request
						.getAttribute("duration")));
				ad.setUploadComplete(uploadComplete);
				ad.setPriority(AdPriority.valueOfIntString(request
						.getAttribute("priority")));
				ad.setStoreType(AdStoreType.valueOfIntString(request
						.getAttribute("storetype")));
				ad.setFileName(item.getRealFileName());
				ad.setUrl(processor.getHttpUrl(ad.getFileName()));
				if (request.getAttribute("id") != null) {
					ad.setId(Integer.valueOf((String) request
							.getAttribute("id")));
					KeyValue<String, BaseTermAd> r = bean.edit(loginUserId, ad);
					processor.deleteFile(loginUserId, r.getKey(), false);
					ad = r.getValue();
				} else {
					ad = bean.add(loginUserId, ad);
					request.setAttribute("id", ad.getId().toString());
				}
				return convertResponse(ad);
			}
			}
		} finally {
			context.close();
		}
		return null;
	}

	public String uploadComplete(long loginUserId,
			FileUploadProcessor processor, HttpServletRequest request,
			BaseFileItem item) throws NamingException {
		short fileCategoryId = processor.getFileCategory().getId();
		LoopNamingContext context = new LoopNamingContext("db");
		try {
			switch (fileCategoryId) {
			case 4: {
				OrgAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-orgAdBean", OrgAdBeanRemote.class);
				BaseOrgAd ad = new BaseOrgAd(Integer.valueOf((String) request
						.getAttribute("id")));
				ad.setUploadComplete(true);
				return convertResponse(bean.updateUpload(loginUserId, ad));
			}
			case 5: {
				TermAdBeanRemote bean = context.lookup(Lookuper.JNDI_TYPE_EJB,
						"kxd-ejb-termAdBean", TermAdBeanRemote.class);
				BaseTermAd ad = new BaseTermAd(Integer.valueOf((String) request
						.getAttribute("id")));
				ad.setUploadComplete(true);
				return convertResponse(bean.updateUpload(loginUserId, ad));
			}
			}
		} finally {
			context.close();
		}
		return null;
	}
}
