package ramBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import sx.blah.discord.api.*;
import sx.blah.discord.api.events.*;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

public class RamBot extends BaseBot implements IListener<MessageReceivedEvent> {
	
	ApiAiClient aiClient;
	public IUser sunkink29;
	public IUser botManager;
	public IChannel sunkink29Dm;
	List<String> admins = new ArrayList<String>();
	private List<Command> commands = new ArrayList<>();
	
	public static void main(String[] args) {
		BaseBot.main(args);
	}

	public RamBot(IDiscordClient discordClient, ApiAiClient aiClient) {
		super(discordClient);
		this.aiClient = aiClient;
		EventDispatcher dispatcher = discordClient.getDispatcher(); // Gets the client's event dispatcher
		dispatcher.registerListener(this); // Registers this bot as an event listener
		admins.add("194936758696148992");
		admins.add("285607196245622785");
		
		addCommand("help", message -> commands.forEach(command -> sendMessage(message.message.getChannel(), command.name)));
		
		addCommand("sendMessage", message -> {
					List<String> words = new ArrayList<String>(Arrays.asList(message.content.split("\\s+")));
					List<IChannel> channels = new ArrayList<>();
					List<String> guilds = new ArrayList<>();
					for (IGuild guild: client.getGuilds()) { guilds.add(guild.getID());}
					words.stream().filter(word -> (word.contains("<") || (word.split("\\.").length > 1 && guilds.contains(word.split("\\.")[0]))) && 
							!word.contains(client.getOurUser().getID()))
					.forEach(channel ->{ 
						if (channel.contains("<")) {
							channels.add(getPMChannel(client.getUserByID(channel.substring(2, channel.length()-1))));
						} else {
							client.getGuilds().stream().filter(nChannel -> nChannel.getID().equals(channel.split("\\.")[0]))
								.forEach(nChannel -> channels.add(nChannel.getChannelByID(channel.split("\\.")[1])));
						}
					});
					String sMessage = "";
					for(String word: words){if (word.contains("\"")) sMessage = word.substring(1, word.length()-1);}
					for(IChannel channel: channels) { sendMessage(channel, sMessage);}
				});
		
		addCommand("addAdmin", message -> message.message.getMentions().stream()
				.filter(mentionClient -> mentionClient != client.getOurUser() && !admins.contains(mentionClient.getID()))
				.forEach(user -> admins.add(user.getID())));
		
		addCommand("removeAdmin", message -> message.message.getMentions().stream()
				.filter(mentionClient -> mentionClient != client.getOurUser() && !admins.contains(mentionClient.getID()))
				.forEach(user -> admins.remove(user.getID())));
		
		addCommand("printAdmins", message -> admins.forEach(admin ->
				sendMessage(message.message.getChannel(), client.getUserByID(admin).getName())));
		
		addCommand("getGuild", message -> client.getGuilds()
				.forEach(guild -> sendMessage(message.message.getChannel(), guild.getName()+" "+guild.getID())));
		
		addCommand("getUsers", message -> client.getUsers()
				.forEach(user -> sendMessage(message.message.getChannel(), user.getName())));
		
		addCommand("logout", message -> {sendMessage(message.message.getChannel(), "logging out");logout();});
		
		addCommand("roll", message -> {
					String[] words = message.args;
					int numberOfDice = Integer.parseInt(words[0]);
					int numberOfSides = Integer.parseInt(words[1].substring(1));
					List<Short> diceRolls = new ArrayList<>();
					for (int i=0;i<numberOfDice;i++) diceRolls.add((short)(Math.random()*numberOfSides+1));
					String output = "";
					if (diceRolls.size() < 20) {
						output = "I rolled a ";
					for (int i=0;i<diceRolls.size();i++) output+= diceRolls.get(i) + (i+1==diceRolls.size()?" ":", ");
					}else {
						output = "I rolled " + diceRolls.size() + " dice ";
					}
					int total = diceRolls.parallelStream().mapToInt(die -> die.intValue()).sum();
					output += (diceRolls.size()>1?"for a total of "+total:"");
					sendMessage(message.message.getChannel(), output);
				;});
		
		addCommand("flip", message -> {
					String[] words = message.args;
					int numberOfcoins = Integer.parseInt(words[0]);
					int total = 0;
					for (int i=0;i<numberOfcoins;i++) total+=(int)(Math.random()*2);
					String output = "I flipped " + (numberOfcoins==1?(total==1?"heads":"tails"):
						total+" heads and "+(numberOfcoins-total)+" tails");
					sendMessage(message.message.getChannel(), output);
				;});
	}

	/**
	 * Called when the client receives a message.
	 */
	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage(); // Gets the message from the event object NOTE: This is not the content of the message, but the object itself
		IChannel channel = message.getChannel(); // Gets the channel in which this message was sent.
		String output = "";
		if (message.getMentions().contains(client.getOurUser()) || message.getChannel().isPrivate()) {
			System.out.println(message.getAuthor().getName()+ " : " + message.getContent());
			boolean commandTriggered = false;
			if (admins.contains(message.getAuthor().getID()) && message.getContent().contains("!")) {
				for(int i = 0; i < commands.size(); i++) {
					if (commands.get(i).checkCondition(new Message(message))) {
						commands.get(i).execute(new Message(message));
						commandTriggered = true;
						break;
					}
				}
			}
			
			if(!commandTriggered) {
				String content = message.getContent().toLowerCase();
				List<String> words = new ArrayList<String>(Arrays.asList(content.split("\\s+")));
				List<String> words2 = new ArrayList<>(words); 
				words.stream().filter(word -> word.contains("<@")).forEach(word -> 
					{int i = words.indexOf(word); words2.remove(word);
					words2.add(i, client.getUserByID(word.substring(2, word.length()-1)).getName());});
				content = String.join(" ", words2);
				content = content.replace("<@"+client.getOurUser().getID()+">", "");
				output = aiClient.getResponce(content);
				if (output.contains("!")) {
					for(int i = 0; i < commands.size(); i++) {
						if (commands.get(i).checkCondition(new Message(message,output))) {
							commands.get(i).execute(new Message(message,output));
							commandTriggered = true;
							break;
						}
					}
				}
				if (!commandTriggered) {
					sendMessage(channel, output);
				}
			}
			
		}
	}
	
	public void addCommand(String name, Consumer<Message> action) {
		if (commands.stream().filter(command -> command.name == name).count() == 0) {
			commands.add(new Command(name, action));
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
	
	private IChannel getPMChannel(IUser user){
		try {
			return client.getOrCreatePMChannel(user);
		} catch (DiscordException | RateLimitException e) {
			e.printStackTrace();
			return null;
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

class Message {
	public final IMessage message;
	public final String content;
	public String[] args = new String[1];
	
	public Message (IMessage message) {
		this(message, message.getContent());
	}
	
	public Message (IMessage message, String content) {
		this.message = message;
		this.content = content;
	}
}

class Command{
	private final Consumer<Message> action;
	public final String name;
	
	public Command(String name, Consumer<Message> action) {
		this.action = action;
		this.name = name;
	}
	
	public boolean checkCondition(Message message){
		return message.content.contains("!"+name);
	}
	
	public void execute(Message message){
		List<String> words = new ArrayList<String>(Arrays.asList(message.content.split("\\s+")));
		if (words.size() > 0) {
			while (!words.get(0).equals("!"+name)){
				words.remove(0);
			}
			words.remove(0);
		}
		message.args = words.toArray(message.args);
		action.accept(message);
	}
}
