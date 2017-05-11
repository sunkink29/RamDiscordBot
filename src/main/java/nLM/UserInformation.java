package nLM;

import java.util.ArrayList;

import sx.blah.discord.handle.obj.IUser;

public class UserInformation extends Noun {
	
	public IUser user;
	
	public UserInformation(IUser user) {
		super(user.getName(), new ArrayList<String>(), "user");
	}
	
}
