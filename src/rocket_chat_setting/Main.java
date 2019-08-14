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

	@Parameter(description = "setting_id [valueToSet]")
	List<String> parameters = new ArrayList<>();

	private RocketChatClient rcc;

	public static void main(String... argv) {
		Main main = new Main();
		JCommander commander = JCommander.newBuilder().addObject(main).build();
		try {
			commander.parse(argv);
			main.run();
		} catch (ParameterException e) {
			System.out.println(e.getMessage()+"\n");
			commander.usage();
		}
	}

	public void run() {
		if (parameters.isEmpty()) {
			return;
		}

		rcc = new RocketChatClient(serviceUrl, user, password);
		if (trustSelfSignedCertificate) {
			try {
				rcc.trustSelfSignedCertificates();
			} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		String output = null;

		try {
			if (parameters.size() == 1) {
				// getSetting
				Setting setting = rcc.getSettingsApi().getById(parameters.get(0));
				if (setting != null) {
					if (verbose) {
						output = "[" + parameters.get(0) + "] " + setting.getValue();
					} else {
						output = setting.getValue();
					}

				} else {
					output = "setting [" + parameters.get(0) + "] not found";
				}
			} else {
				// setSetting
				rcc.getSettingsApi().setById(parameters.get(0), parameters.get(1));
				if (verbose) {
					output = "[" + parameters.get(0) + "] -> " + parameters.get(1);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		if (output != null) {
			System.out.println(output);
		}
	}
}
