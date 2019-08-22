package rocket_chat_setting;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
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

	@Parameter(names = "-s", description = "Enable set mode, expects key/value tuples a parameters")
	boolean set = false;

	@Parameter(description = "[ setting_key ]... | if set mode: [ setting_key setting_value ]...")
	List<String> parameters = new ArrayList<>();

	private RocketChatClient rcc;

	public static void main(String... argv) {
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

	public void run() {
		if (parameters.isEmpty()) {
			return;
		}

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

			if (set) {

				if (parameters.size() == 1) {
					output.append("Single parameter, invalid -s argument");
					exit(output);
				}

				if ((parameters.size() % 2) != 0) {
					output.append("Non-even parameters size length");
					exit(output);
				}

				Iterator<String> iterator = parameters.iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					String value = iterator.next();

					try {
						setParameter(rcc.getSettingsApi(), output, key, value);
					} catch (IOException ioe) {
						output.append("setting [" + key + "] failed: " + ioe.getMessage() + "\n");
					}
				}

			} else {

				for (String key : parameters) {
					Setting setting = rcc.getSettingsApi().getById(key);
					if (setting != null) {
						if (verbose) {
							output.append("[" + key + "] " + setting.getValue() + "\n");
						} else {
							output.append(setting.getValue() + "\n");
						}

					} else {
						output.append("setting [" + key + "] not found\n");
					}
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

	private void exit(StringBuilder output) {
		System.out.println(output);
		System.exit(0);

	}

	private void setParameter(RocketChatRestApiV1Settings rocketChatRestApiV1Settings, StringBuilder output, String key,
			String value) throws IOException {
		rcc.getSettingsApi().setById(key, value);
		if (verbose) {
			output.append("[" + key + "] -> " +value + "\n");
		}
	}

}
