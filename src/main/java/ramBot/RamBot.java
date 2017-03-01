package ramBot;

import java.util.ArrayList;
import java.util.List;

import nLM.NaturalLanguageModule;
import nLM.NaturalLanguageParser;
import nLM.OldNaturalLanguageModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


public class RamBot extends BaseBot implements IListener<MessageReceivedEvent> {
	
	OldNaturalLanguageModule nAModule;
	public IUser sunkink29;
	public IUser botManager;
	public IChannel sunkink29Dm;
	
	public static void main(String[] args) {
		BaseBot.main(args);
	}

	public RamBot(IDiscordClient discordClient) {
		super(discordClient);
//		ReadyEventListener rEventListener = new ReadyEventListener();
		EventDispatcher dispatcher = discordClient.getDispatcher(); // Gets the client's event dispatcher
		dispatcher.registerListener(this); // Registers this bot as an event listener
//		dispatcher.registerListener(rEventListener);
		nAModule = new OldNaturalLanguageModule();
	}

	/**
	 * Called when the client receives a message.
	 */
	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage(); // Gets the message from the event object NOTE: This is not the content of the message, but the object itself
		IChannel channel = message.getChannel(); // Gets the channel in which this message was sent.
		if (message.getMentions().contains(client.getOurUser())) {
			System.out.println(message.getAuthor().getName()+ " : " + message.getContent());
			if (sunkink29 == null)
				sunkink29 = client.getUserByID("194936758696148992");
			if (message.getContent().contains("!getGuild") && message.getAuthor().equals(sunkink29)) {
				List<IGuild> guilds = client.getGuilds();
				String output = "";
				for (int i = 0; i < guilds.size(); i++) {
					output += guilds.get(i).getName()+", ";
				}
				sendMessage(channel, output);
			} else if (message.getContent().toLowerCase().contains("!parser") && message.getAuthor().equals(sunkink29)){
				String content = message.getContent().toLowerCase();
				content = content.replace("!parser", "");
				content = content.replace("<@"+client.getOurUser().getID()+">", "");
				ArrayList<String> output = NaturalLanguageParser.getResponse(content);
				for (int i = 0; i < output.size(); i++) {
					sendMessage(channel, output.get(i));
				}
				
			} else if (message.getContent().contains("logout") && (message.getAuthor().equals(sunkink29)) || message.getAuthor().equals(botManager)) {
				logout();
			} else {
				sendMessage(channel, nAModule.getResponse(message.getContent()));
			}
		}
	}
	
	public void sendMessage(IChannel channel, String message) {
		boolean retry = true;
		while (retry) {
			retry = false;
			try {
				// Builds (sends) and new message in the channel that the original message was sent with the content of the original message.
				new MessageBuilder(this.client).withChannel(channel).withContent(message).build();
			} catch (RateLimitException e) { // RateLimitException thrown. The bot is sending messages too quickly!
				System.err.println("Sending messages too quickly!");
				//e.printStackTrace();
				try {
					synchronized (this) {
						wait(e.getRetryDelay());
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				retry = true;
			} catch (DiscordException e) { // DiscordException thrown. Many possibilities. Use getErrorMessage() to see what went wrong.
				System.err.print(e.getErrorMessage()); // Print the error message sent by Discord
				e.printStackTrace();
			} catch (MissingPermissionsException e) { // MissingPermissionsException thrown. The bot doesn't have permission to send the message!
				System.err.print("Missing permissions for channel!");
				e.printStackTrace();
			}
		}
	}
	
	private void logout() {
		try {
			client.logout();
		} catch (DiscordException e) {
			e.printStackTrace();
		}
	}
}
