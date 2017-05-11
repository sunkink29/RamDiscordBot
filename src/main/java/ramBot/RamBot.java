package ramBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.Attributes.Name;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.impl.obj.User;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;


public class RamBot extends BaseBot implements IListener<MessageReceivedEvent> {
	
	public IUser sunkink29;
	public IUser botManager;
	public IChannel sunkink29Dm;
	List<String> admins = new ArrayList<String>();
	private List<String> actionNames = new ArrayList<String>(); 
	private List<Predicate<IMessage>> actionConditions = new ArrayList<Predicate<IMessage>>();
	private List<Consumer<IMessage>> actionList = new ArrayList<>();
	
	public static void main(String[] args) {
		BaseBot.main(args);
	}

	public RamBot(IDiscordClient discordClient) {
		super(discordClient);
		EventDispatcher dispatcher = discordClient.getDispatcher(); // Gets the client's event dispatcher
		dispatcher.registerListener(this); // Registers this bot as an event listener
		admins.add("194936758696148992");
		admins.add("285607196245622785");
		
		addAction("help", message -> message.getContent().contains("!help"), 
				message -> actionNames.forEach(name -> sendMessage(message.getChannel(), name)));
		
		addAction("sendMessage", message -> message.getContent().contains("!sendMessage"),
				message -> {
					List<String> words = new ArrayList<String>(Arrays.asList(message.getContent().split("\\s+")));
					List<IChannel> channels = new ArrayList<>();
					List<String> guilds = new ArrayList<>();
					for (IGuild guild: client.getGuilds()) { guilds.add(guild.getID());}
					words.stream().filter(word -> (word.contains("<") || (word.split("\\.").length > 1 && guilds.contains(word.split("\\.")[0]))) && 
							!word.contains(client.getOurUser().getID()))
					.forEach(channel ->{ 
						if (channel.contains("<")){
							try {
								channels.add(client.getOrCreatePMChannel(client.getUserByID(channel.substring(2, channel.length()-1))));
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							client.getGuilds().stream().filter(nChannel -> nChannel.getID().equals(channel.split("\\.")[0]))
								.forEach(nChannel -> channels.add(nChannel.getChannelByID(channel.split("\\.")[1])));
						}
					});
					String sMessage = "";
					for(String word: words){if (word.contains("\"")) sMessage = word.substring(1, word.length()-1);}
					for(IChannel channel: channels) { sendMessage(channel, sMessage);}
				});
		
		addAction("addAdmin", message -> message.getContent().contains("!addAdmin"),
				message -> message.getMentions().stream()
				.filter(mentionClient -> mentionClient != client.getOurUser() && !admins.contains(mentionClient.getID()))
				.forEach(user -> admins.add(user.getID())));
		
		addAction("printAdmins", message -> message.getContent().contains("!printAdmins"),
				message -> admins.forEach(admin -> sendMessage(message.getChannel(), client.getUserByID(admin).getName())));
		
		addAction("getGuild", message -> message.getContent().contains("!getGuild"),
				message -> client.getGuilds().forEach(guild -> sendMessage(message.getChannel(), guild.getName()+" "+guild.getID())));
		
		addAction("getUsers", message -> message.getContent().contains("!getUsers")
				, message -> client.getUsers().forEach(user -> sendMessage(message.getChannel(), user.getName())));
		
		addAction("logout", message -> message.getContent().contains("!logout"),
				message -> {sendMessage(message.getChannel(), "logging out");logout();});
	}

	/**
	 * Called when the client receives a message.
	 */
	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage(); // Gets the message from the event object NOTE: This is not the content of the message, but the object itself
		IChannel channel = message.getChannel(); // Gets the channel in which this message was sent.
		if (message.getMentions().contains(client.getOurUser()) || message.getChannel().isPrivate()) {
			System.out.println(message.getAuthor().getName()+ " : " + message.getContent());
			
			if (admins.contains(message.getAuthor().getID()) && message.getContent().contains("!")) {
				for(int i = 0; i < actionConditions.size(); i++) {
					if (actionConditions.get(i).test(message)) {
						actionList.get(i).accept(message);
						break;
					}
				}
			} else {
				String content = message.getContent().toLowerCase();
				content = content.replace("<@"+client.getOurUser().getID()+">", "");
				String output = content;
				sendMessage(channel, output);
			}
		}
	}
	
	public void addAction(String name, Predicate<IMessage> condition, Consumer<IMessage> action) {
		if (!actionNames.contains(name)) {
			actionNames.add(name);
			actionConditions.add(condition);
			actionList.add(action);
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
				retry = true;
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
