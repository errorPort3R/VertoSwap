package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.services.*;
import com.theironyard.utilities.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.theironyard.entities.Item.Status.*;



/**
 * Created by Dan on 7/19/16.
 */
@Controller
public class VertoSwapController
{

    public static String PHOTOS_DIR = "photos/";
    public static final String  USER_FILE = "demovs1users.txt";
    public static final String  ITEM_FILE = "demovs1items.txt";
    public static final String  WORK_FILE = "demovs1works.txt";
    public static final String  MESSAGE_FILE = "demovs1messages.txt";
    public static final String  PHOTO_FILE = "demovs1photo.txt";
    public static State state = new State();

    @Autowired
    UserRepository users;

    @Autowired
    WorkRepository works;

    @Autowired
    ItemRepository items;

    @Autowired
    MessageaRepository messages;

    @Autowired
    PhotoRepository photos;



    @PostConstruct
    public void init() throws SQLException, IOException, PasswordStorage.CannotPerformOperationException
    {
        if (users.count() == 0)
        {
            migrateTextFiles();
        }

    }


    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String main(HttpSession session, Model model, String search) {
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        Iterable<Item> searchList;
        ArrayList<Item> searchServiceArray = new ArrayList<>();
        ArrayList<Item> searchGoodArray = new ArrayList<>();
        if (search != null) {
            searchList = items.searchText(search, search, search, search);
            //searchList = items.findByTitleLikeOrLocationLikeOrDescriptionLikeOrAcceptableExchangeLike(search, search, search, search);
            for (Item i : searchList) {
                if (!i.isService()) {
                    searchGoodArray.add(i);
                }
                else if (i.isService()) {
                    searchServiceArray.add(i);
                }
                model.addAttribute("goods", searchGoodArray);
                model.addAttribute("services", searchServiceArray);
            }
        }
        else {
            ArrayList<Item> s = new ArrayList<>();
            Iterable<Item> servicesList = items.findByServiceTrueOrderByTimeDesc();
            for (Item i : servicesList) {
                if (i.getStatus() != DELETE) {
                    s.add(i);
                    model.addAttribute("services", s);
                }
            }
            ArrayList<Item> g = new ArrayList<>();
            Iterable<Item> goodsList = items.findByServiceFalseOrderByTimeDesc();
            for (Item i : goodsList) {
                if (i.getStatus() != DELETE) {
                    g.add(i);
                    model.addAttribute("goods", g);
                }
            }
        }
        return "home";
    }

    @RequestMapping(path = "/account-create", method = RequestMethod.GET)
    public String createAccountPage(HttpSession session, Model model)
    {
        return "account-create";
    }

    @RequestMapping(path = "/user-profile", method = RequestMethod.GET )
    public String userProfile(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        //Iterable<Item> activeItems = items.findByUser(user);
        Iterable<Item> activeItems = items.findByUserAndStatusOrderByTimeDesc(user, ACTIVE);
        //Iterable<Item> activeItems = items.findByUserAndStatus(user, ACTIVE);
        Iterable<Item> inactiveItems = items.findByUserAndStatusOrderByTimeDesc(user, INACTIVE);
        //Iterable<Item> inactiveItems = items.findByUserAndStatus(user, INACTIVE);
        Iterable<Messagea> messagesList = messages.findByRecipient(user);
        model.addAttribute("username", username);
        model.addAttribute("activeBarters", activeItems);

        //  to show photo/work upload only if good/service resp. :
        for (Item i : activeItems) {
            if (i.isService()) {
                model.addAttribute("service", i);
            }
            else {
                model.addAttribute("service", i);
            }
        }
        Iterable<Work> workHistory = works.findByUser(user);
        model.addAttribute("workHistory", workHistory);
        model.addAttribute("messages", messagesList);

        return "user-profile";
    }

    @RequestMapping(path = "/archive", method = RequestMethod.GET)
    public String archive(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Iterable<Item> archivedItems = items.findByUserAndStatus(user, ARCHIVE);
        model.addAttribute("username", username);
        model.addAttribute("archived", archivedItems);
        return "archive";
    }

    @RequestMapping(path = "/work-history", method = RequestMethod.GET)
    public String workHistory(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        Iterable<Work> workList = works.findByUser(user);
        model.addAttribute("workList", workList);
        model.addAttribute("username", username);
        return "work-history";
    }

    @RequestMapping(path = "/update-item", method = RequestMethod.GET)
    public String updateItem(HttpSession session, Model model, int id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        Item item = items.findOne(id);
        model.addAttribute("title", item.getTitle());
        model.addAttribute("description", item.getDescription());
        model.addAttribute("location", item.getLocation());
        model.addAttribute("acceptableExchange", item.getAcceptableExchange());
        model.addAttribute("service", item.isService());
        model.addAttribute("id", item.getId());

        return "update-item";
    }

    @RequestMapping(path = "/update-work", method = RequestMethod.GET)
    public String updateWork(HttpSession session, Model model, int id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        Work work = works.findOne(id);
        model.addAttribute("jobTitle", work.getJobTitle());
        model.addAttribute("description", work.getDescription());
        model.addAttribute("id", work.getId());
        return "update-work";
    }




    //***************************************************************************************
    //
    //USER ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/account-create", method = RequestMethod.POST)
    public String createAccount(HttpSession session, String username, String password) throws PasswordStorage.CannotPerformOperationException
    {
        User user = users.findByUsername(username);
        if (user != null) {
            System.err.printf("User Already Exists!");
            return "redirect:/";
        }
        else {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
            session.setAttribute("username", username);
            return "redirect:/user-profile";
        }
    }

    @RequestMapping(path = "/account-update", method = RequestMethod.POST)
    public String editAccount(HttpSession session, String password, String newUsername, String newPassword) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByUsername(username);
        if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password.");
        }
        user.setUsername(newUsername);
        user.setPassword(PasswordStorage.createHash(newPassword));
        users.save(user);
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/account-delete", method = RequestMethod.POST)
    public String deleteAccount(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findByUsername(username);
        items.delete(items.findByUser(user));
        works.delete(works.findByUser(user));
        photos.delete(photos.findByUser(user));
        messages.delete(messages.findByAuthor(user));
        messages.delete(messages.findByRecipient(user));
        users.delete(user.getId());
        return "redirect:/";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String username, String password, HttpServletRequest request) throws Exception {
        User userFromDB = users.findByUsername(username);
        if (userFromDB == null)
        {
            return "redirect:/account-create";
        }
        else if (!PasswordStorage.verifyPassword(password, userFromDB.getPassword()))
        {
            throw new Exception("Wrong password.");
        }
        session.setAttribute("username", username);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
//        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }


    //***************************************************************************************
    //
    //WORK ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/work-create", method = RequestMethod.POST)
    public String createWork(HttpSession session,String jobTitle, String description)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Work w = new Work(jobTitle, description, user);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/work-history";
    }

    @RequestMapping(path = "/work-read", method = RequestMethod.GET)
    public String getWork(HttpSession session)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Work> workList = works.findByUser(user);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/work-update", method = RequestMethod.POST)
    public String updateWork(HttpSession session, int id, String jobTitle, String description)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Work w = new Work(jobTitle, description, user);
        w.setId(id);
        works.save(w);
        session.setAttribute("username", user.getUsername());
        return "redirect:/work-history";
    }

    @RequestMapping(path = "/work-delete", method = RequestMethod.POST)
    public String deleteWork(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/work-history";
    }

    //***************************************************************************************
    //
    //                  ITEM ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/item-create", method = RequestMethod.POST)
    public String createItem(HttpSession session, String title, String location, String description, String acceptableExchange, boolean service)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        LocalDateTime time = LocalDateTime.now();
        //Item.Status status = ACTIVE;
        Item i = new Item(title, location, description, acceptableExchange, ACTIVE, time, service, user);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/view-item", method = RequestMethod.GET)
    public String getSpecificItem(HttpSession session, Model model,@RequestParam String id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        model.addAttribute("username", username);

        User user = users.findByUsername(username);
        Item item = items.findOne(Integer.valueOf(id));
//        if (user == null)
//        {
//            return "redirect:/";
//        }
//        session.setAttribute("username", user.getUsername());
        model.addAttribute("good", item);

        if(item.isService()) {
            model.addAttribute("service", true);
        }

        Iterable<Photo> photoIterable = photos.findByItem(item);
        ArrayList<Photo> photoArrayList = new ArrayList<>();
        for (Photo p : photoIterable) {
            photoArrayList.add(p);
            model.addAttribute("photoActive", photoArrayList.get(0));
            model.addAttribute("photos", photoArrayList.subList(1, photoArrayList.size()));
        }


        return "view-barter";
    }

    @RequestMapping(path = "/item-read", method = RequestMethod.GET)
    public Iterable<Item> getItem(HttpSession session)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Item> itemsList = items.findByUser(user);
        for(Item item : itemsList)
        {
            if(item.getTime().plusWeeks(2).compareTo(LocalDateTime.now()) > 0)
            {
                item.setStatus(INACTIVE);
                items.save(item);
            }
        }
        session.setAttribute("username", user.getUsername());
        return itemsList;
    }

    @RequestMapping(path = "/item-attach-work", method = RequestMethod.POST)
    public String attachWork(HttpSession session, Integer workId, Integer id) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        if (workId != null && id != null)
        {
            Work work = works.findOne(workId);
            Item i = items.findOne(id);
            i.setWork(work);
            items.save(i);
        }
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/item-update", method = RequestMethod.POST)
    public String updateItem(HttpSession session, int id, String title, String location, String description, String acceptableExchange, String stat, boolean service)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        LocalDateTime time = LocalDateTime.now();
        //Item.Status status = Item.Status.valueOf(stat);
        Item i = new Item(title, location, description, acceptableExchange, ACTIVE, time, service, user);
        i.setId(id);
        items.save(i);
        session.setAttribute("username", user.getUsername());
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/item-delete", method = RequestMethod.POST)
    public String deleteitem (HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        //items.delete(id);
        Item item = items.findOne(id);
        item.setStatus(DELETE);
        items.save(item);
        //session.setAttribute("username", user.getUsername());
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/item-archive", method = RequestMethod.POST)
    public String archiveItem(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String) session.getAttribute("username");
        Item item = items.findOne(id);
        item.setStatus(ARCHIVE);
        items.save(item);
        return "redirect:/user-profile";
    }


    //***************************************************************************************
    //
    //           PHOTO ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/photo-create", method = RequestMethod.POST)
    public String addPhoto(HttpSession session, MultipartFile photo, String filename, String caption, /*Item item,*/ int id, HttpServletResponse response) throws Exception
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        File dir = new File("public/" + PHOTOS_DIR);
        dir.mkdirs();

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Item item = items.findOne(id);

        Photo newPhoto = new Photo(photoFile.getName(), caption, user, item);
        photos.save(newPhoto);
        session.setAttribute("username", user.getUsername());
        return "redirect:/user-profile";
    }

    @RequestMapping(path = "/photo-read", method = RequestMethod.GET)
    public String getPhoto(HttpSession session, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Photo> photoList = photos.findByItem(item);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    @RequestMapping(path = "/photo-update", method = RequestMethod.POST)
    public String updatePhoto(HttpSession session, MultipartFile photo, int id, String filename, String caption, Item item, HttpServletResponse response) throws Exception
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        File dir = new File("public/" + PHOTOS_DIR);
        dir.mkdirs();

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), dir);
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo newPhoto = new Photo(photoFile.getName(), caption, user, item);
        newPhoto.setId(id);
        photos.save(newPhoto);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/photo-delete", method = RequestMethod.POST)
    public String deletePhoto(HttpSession session, int id)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        works.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }

    //***************************************************************************************
    //
    //                  MESSAGE ROUTES
    //
    //***************************************************************************************
    @RequestMapping(path = "/message-reply", method = RequestMethod.POST)
    public String messageFromBuyer(HttpSession session, Model model, String body, int itemid, int receiverid, String conversation)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Item item = items.findOne(itemid);
        User receiver = users.findOne(receiverid);
        Messagea m = new Messagea(user, receiver, item, body, LocalDateTime.now(), conversation);
        messages.save(m);
        session.setAttribute("username", user.getUsername());
        return "redirect:/thread-read-all";
    }


    @RequestMapping(path = "/message-to-seller", method = RequestMethod.POST)
    public String messagetoseller(HttpSession session,@RequestParam String itemId,@RequestParam String body)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Item item = items.findOne(Integer.valueOf(itemId));
        //create conversation key
        String conversation = String.format("%s_%s", itemId, user.getId());
        Messagea m = new Messagea(user, item.getUser(), item, body, LocalDateTime.now(), conversation);
        messages.save(m);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/message-get-by-conversation", method = RequestMethod.GET)
    public String getMessages(HttpSession session, Model model, String conversation)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        state.setConversation(conversation);
        List<Messagea> messageList = messages.findByConversation(conversation);
        Collections.sort(messageList);
        //get variables for page

        User receiver = new User();
        int itemId = messageList.get(messageList.size()-1).getItem().getId();
        String conKey = messageList.get(messageList.size()-1).getConversation();
        if (user.getId() == messageList.get(messageList.size()-1).getItem().getUser().getId())
        {
            receiver = messageList.get(messageList.size()-1).getAuthor();
        }
        else
        {
            receiver = messageList.get(messageList.size()-1).getItem().getUser();
        }
        Collections.sort(messageList);
        session.setAttribute("username", user.getUsername());
        model.addAttribute("name", user.getUsername());
        model.addAttribute("conkey", conKey);
        model.addAttribute("itemid", itemId);
        model.addAttribute("receiverid", receiver.getId());
        model.addAttribute("messages", messageList);
        return "message-display";
    }

    @RequestMapping(path = "/thread-read-all", method = RequestMethod.GET)
    public String getConversation(HttpSession session, Model model, ArrayList<String> conversationId)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        ArrayList<Messagea> messageList = new ArrayList();
        HashMap<String ,Messagea> mapList= new HashMap<>();
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        List<Messagea> messageLista = messages.findByRecipient(user);
        List<Messagea> messageListb = messages.findByAuthor(user);
        for (Messagea b: messageListb)
        {
            messageLista.add(b);
        }
        Collections.sort(messageLista);

        for(Messagea m : messageLista)
        {
            mapList.put(m.getConversation(), m);
        }
        Iterator iter = mapList.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry pair = (Map.Entry)iter.next();
            messageList.add((Messagea)pair.getValue());
            iter.remove();
        }

        session.setAttribute("username", user.getUsername());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("conversations", messageList);
        return "message-list";
    }

    @RequestMapping(path = "/message-update", method = RequestMethod.POST)
    public String updateMessage(HttpSession session,int id, Messagea message)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        message.setId(id);
        messages.save(message);
        session.setAttribute("username", user.getUsername());
        return "chatpage";
    }

    @RequestMapping(path = "/message-delete", method = RequestMethod.POST)
    public String deleteMessage(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        messages.delete(id);
        session.setAttribute("username", user.getUsername());
        return "redirect:/";

    }

    @RequestMapping(path = "/conversation-delete", method = RequestMethod.POST)
    public String deleteConversation(HttpSession session, String conversation, HttpServletRequest request)
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
//        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Messagea> messageList = messages.findByConversation(conversation);
        for(Messagea m : messageList)
        {
            messages.delete(m.getId());
        }
        session.setAttribute("username", user.getUsername());

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

    public void migrateTextFiles() throws PasswordStorage.CannotPerformOperationException, IOException
    {
        //User: username|password 1234
        Scanner fileScanner =  new Scanner(new File(USER_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            users.save(new User(fields[0],PasswordStorage.createHash(fields[1])));
        }
        //Item: title|location|description|acceptableExchange|status|service|user
        fileScanner = new Scanner(new File(ITEM_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            items.save(new Item(fields[0],fields[1], fields[2], fields[3], Item.Status.valueOf(fields[4]), LocalDateTime.now(), Boolean.valueOf(fields[5]), users.findOne(Integer.valueOf(fields[6]))));
        }
        //Work:jobTitle|description|user
        fileScanner = new Scanner(new File(WORK_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            works.save(new Work(fields[0], fields[1], users.findOne(Integer.valueOf(fields[2]))));
        }
        //Message:author|recipient|item|body|conversation
        fileScanner = new Scanner(new File(MESSAGE_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            messages.save(new Messagea(users.findOne(Integer.valueOf(fields[1])), users.findOne(Integer.valueOf(fields[0])), items.findOne(Integer.valueOf(fields[2])), fields[3], LocalDateTime.now(), fields[4]));
        }
        //Photo:filename|caption|user|item
        fileScanner = new Scanner(new File(PHOTO_FILE));
        fileScanner.nextLine();
        while (fileScanner.hasNext())
        {
            String line = fileScanner.nextLine();
            String[] fields = line.split("\\|");
            photos.save(new Photo(fields[0],fields[1],users.findOne(Integer.valueOf(fields[2])), items.findOne(Integer.valueOf(fields[3]))));
        }
        fileScanner.close();
    }
}
