package ramBot;
import java.util.Scanner;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * This represents a SUPER basic bot (literally all it does is login).
 * This is used as a base for all example bots.
 */
public class BaseBot {

	public static RamBot INSTANCE; // Singleton instance of the bot.
	public IDiscordClient client; // The instance of the discord client.

	public static void main(String[] args) { // Main method
		if (args.length < 1) // Needs a bot token provided
			throw new IllegalArgumentException("This bot needs at least 1 argument!");

		INSTANCE = login(args[0],args[1]); // Creates the bot instance and logs it in.
	}

	public BaseBot(IDiscordClient client) {
		this.client = client; // Sets the client instance to the one provided
	}

	/**
	 * A custom login() method to handle all of the possible exceptions and set the bot instance.
	 */
	public static RamBot login(String token, String aiToken) {
		RamBot bot = null; // Initializing the bot variable

		ClientBuilder builder = new ClientBuilder(); // Creates a new client builder instance
		builder.withToken(token); // Sets the bot token for the client
		IDiscordClient client = null;
		try {
			client = builder.login();
			ApiAiClient aiClient = new ApiAiClient(aiToken);
			bot = new RamBot(client, aiClient); // Creating the bot instance
		} catch (DiscordException e) { // Error occurred logging in
			System.err.println("Error occurred while logging in!");
			e.printStackTrace();
		}
//		boolean retry = true;
//		while(retry) {
//			retry = false;
//			try {
//				client.login();
//			} catch (NullPointerException e) {
//				System.err.println("nullPointer error");
//				retry = true;
//			} catch (DiscordException e) {
//				e.printStackTrace();
//			} catch (RateLimitException e) {
//				e.printStackTrace();
//			}
//		}

		return bot;
	}
}
