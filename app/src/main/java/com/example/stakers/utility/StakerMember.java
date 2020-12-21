package com.example.stakers.utility;

import java.io.Serializable;

public class StakerMember implements Serializable {
                private String userEmail, userPic;
                private boolean isMaster;

                public StakerMember() {

                }

                public StakerMember(boolean isMaster, String userEmail, String userPic) {
                                this.isMaster = isMaster;
                                this.userEmail = userEmail;
                                this.userPic = userPic;
                }

                public String getUserEmail() {
                                return userEmail;
                }

                public void setUserEmail(String userEmail) {
                                this.userEmail = userEmail;
                }

                public String getUserPic() {
                                return userPic;
                }

                public void setUserPic(String userPic) {
                                this.userPic = userPic;
                }

                public boolean isMaster() {
                                return isMaster;
                }

                public void setMaster(boolean master) {
                                isMaster = master;
                }
}
