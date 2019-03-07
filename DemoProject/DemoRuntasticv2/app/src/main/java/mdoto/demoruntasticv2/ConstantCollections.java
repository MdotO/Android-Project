package mdoto.demoruntasticv2;

/**
 * Class for specifying Global Constants
 */
public class ConstantCollections {
    //delay before rerouting to login page
    public static int DelayMillisLoginPage = 4000;
    // Start activity code for Choosing iamge from gallery
    public static int Pick_Image = 0;
    public static class User_Constants{
        public static String profile_pic_key = "Profile_pic";
        public static String name_key = "Name";
        public static String status_key = "Status";
        public static String location_key = "Location";
        public static String profile_pic_file_name_jpeg ="profile_pic.jpg";
    }
    public static class NonUser_Constants{
        public static String profile_pic_key = "Profile_pic";
        public static String name_key = "Name";
        public static String status_key = "Status";
        public static String location_key = "Location";
        public static String ID ="id";
    }
    public static class Message{
        public static String message_key= "message";
        public static String senderID_key="senderID";
        public static String receiverID_key= "receiverID";
    }
    public static class Request{
        public static String title_key= "title";
        public static String description_key="description";
        public static String reward_key= "reward";
        public static String date_key="date_till_valid";
        public static String location_key="location";
        public static String takenupby_key ="taken_up_by_user";
        public static String sender_key="senderID";
    }
}
