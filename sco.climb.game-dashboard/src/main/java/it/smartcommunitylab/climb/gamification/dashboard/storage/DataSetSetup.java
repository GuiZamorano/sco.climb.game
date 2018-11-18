package it.smartcommunitylab.climb.gamification.dashboard.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.climb.gamification.dashboard.security.DataSetInfo;

@Component
public class DataSetSetup {

	@Value("${admin.user}")
	private String adminUser;
	@Value("${admin.token}")
	private String adminToken;
	@Value("${admin.password}")
	private String adminPassword;
	
	@Autowired
	private RepositoryManager storage;	

	@PostConstruct
	public void init() throws IOException {
		this.dataSetList = storage.getDataSetInfo();
		this.dataSetMap = null;
		if (findDataSetById(adminUser) == null) {
			DataSetInfo admin = new DataSetInfo();
			admin.setOwnerId(adminUser);
			admin.setPassword(adminPassword);
			admin.setToken(adminToken);
			storage.saveDataSetInfo(admin);
			dataSetMap.put(admin.getOwnerId(), admin);
			storage.saveAdminToken(adminUser, adminToken);
		}
	}
	

	private List<DataSetInfo> dataSetList;
	private Map<String,DataSetInfo> dataSetMap;

	public List<DataSetInfo> getDataSetInfo() {
		return dataSetList;
	}

	public void setDataSetInfo(List<DataSetInfo> apps) {
		this.dataSetList = apps;
	}

	@Override
	public String toString() {
		return "DataSetSetup [apps=" + dataSetList + "]";
	}

	public DataSetInfo findDataSetById(String username) {
		if (dataSetMap == null) {
			dataSetMap = new HashMap<String, DataSetInfo>();
			for (DataSetInfo app : dataSetList) {
				dataSetMap.put(app.getOwnerId(), app);
			}
		}
		return dataSetMap.get(username);
	}

}
