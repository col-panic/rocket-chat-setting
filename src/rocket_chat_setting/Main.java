package rocket_chat_setting;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.github.baloise.rocketchatrestclient.RocketChatClient;
import com.github.baloise.rocketchatrestclient.RocketChatRestApiV1Settings;
import com.github.baloise.rocketchatrestclient.model.Setting;

public class Main {
	
	@Parameter(names = "-u", required = true, description = "RocketChat service url, e.g. https://rocketchat:3000")
	String serviceUrl;
	
	@Parameter(names = "-l", required = true, description = "username")
	String user;
	
	@Parameter(names = "-p", required = true, description = "password")
	String password;
	
	@Parameter(names = "-t", description = "Trust all HTTPS certificates")
	boolean trustSelfSignedCertificate = false;
	
	@Parameter(names = "-v", description = "Verbose output")
	boolean verbose = false;
	
	@Parameter(names = "-s", description = "values to set as key=value; multiple occurences allowed")
	List<String> set = new ArrayList<>();
	
	@Parameter
	List<String> parameters = new ArrayList<>();
	
	private RocketChatClient rcc;
	
	public static void main(String... argv){
		Main main = new Main();
		JCommander commander = JCommander.newBuilder().addObject(main).build();
		try {
			commander.parse(argv);
			main.run();
		} catch (ParameterException e) {
			System.out.println(e.getMessage() + "\n");
			commander.usage();
		}
	}
	
	public void run(){
		
		rcc = new RocketChatClient(serviceUrl, user, password);
		if (trustSelfSignedCertificate) {
			try {
				rcc.trustSelfSignedCertificates();
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
				if (verbose) {
					e.printStackTrace();
				} else {
					System.out.println(e.getMessage());
				}
				
				System.exit(1);
			}
		}
		
		StringBuilder output = new StringBuilder();
		
		try {
			
			for (String setParamPair : set) {
				if (setParamPair.contains("=")) {
					String[] keyValue = setParamPair.split("=");
					String key = keyValue[0];
					String value = (keyValue.length > 1) ? keyValue[1] : "";
					try {
						setParameter(rcc.getSettingsApi(), output, key, value);
					} catch (IOException ioe) {
						output.append("setting [" + key + "] failed: " + ioe.getMessage() + "\n");
					}
				} else {
					if (verbose) {
						output.append("Invalid set param pair " + setParamPair + "\n");
					}
				}
			}
			
			for (String getParamKey : parameters) {
				Setting setting = rcc.getSettingsApi().getById(getParamKey);
				if (setting != null) {
					if (verbose) {
						output.append("[" + getParamKey + "] " + setting.getValue() + "\n");
					} else {
						output.append(setting.getValue() + "\n");
					}
					
				} else {
					output.append("setting [" + getParamKey + "] not found\n");
				}
			}
			
			rcc.logout();
			
		} catch (IOException e) {
			if (verbose) {
				e.printStackTrace();
			} else {
				System.out.println(e.getMessage());
			}
			
			System.exit(1);
		}
		
		if (output != null) {
			System.out.println(output.toString());
		}
	}
	
	private void setParameter(RocketChatRestApiV1Settings rocketChatRestApiV1Settings,
		StringBuilder output, String key, String value) throws IOException{
		
		Object _value;
		
		if (Boolean.TRUE.toString().equalsIgnoreCase(value)
			|| Boolean.FALSE.toString().equalsIgnoreCase(value)) {
			_value = Boolean.parseBoolean(value);
		} else {
			_value = value;
		}
		
		rcc.getSettingsApi().setById(key, _value);
		if (verbose) {
			output.append("[" + key + "] -> " + _value + "\n");
		}
	}
	
}
