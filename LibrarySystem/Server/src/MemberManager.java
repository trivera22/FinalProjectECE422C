package src;

import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
public class MemberManager {
    private Map<String, String> members;

    public MemberManager(){
        this.members = new HashMap<String, String>();
    }

    public void addMember(String username, String password){
        String hashedPassword = password.isEmpty() ? "" : BCrypt.hashpw(password, BCrypt.gensalt());
        members.put(username, hashedPassword);
    }

    public boolean validateLoginOrRegister(String username, String password){
        //check password if it exists, or succeed if no password is set
        if(members.containsKey(username)) {
            return password.isEmpty() || BCrypt.checkpw(password, members.get(username));
        } else{
            //register new user since they dont exist
            addMember(username, password);
            return true; //login the new user
        }
    }
}
