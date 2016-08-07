package com.theironyard.controllers;

import com.theironyard.entities.Item;
import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by jeffryporter on 8/6/16.
 */
@Controller
public class VSPhotoController
{
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

    //***************************************************************************************
    //
    //           PHOTO ROUTES
    //
    //***************************************************************************************

    //create a photo, transfer file to local /photo directory, save it to the database and reload the page
    @RequestMapping(path = "/photo-create", method = RequestMethod.POST)
    public String addPhoto(HttpSession session, MultipartFile photo, String filename, String caption, int id, HttpServletResponse response) throws Exception
    {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "home";
        }
        User user = users.findByUsername(username);
        File dir = new File("public/" + VertoSwapController.PHOTOS_DIR);
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

    //show photo from original build.  no longer implemented
    @RequestMapping(path = "/photo-read", method = RequestMethod.GET)
    public String getPhoto(HttpSession session, Item item)
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        Iterable<Photo> photoList = photos.findByItem(item);
        session.setAttribute("username", user.getUsername());
        return"";
    }

    //updates a photo in the database
    @RequestMapping(path = "/photo-update", method = RequestMethod.POST)
    public String updatePhoto(HttpSession session, MultipartFile photo, int id, String filename, String caption, Item item, HttpServletResponse response) throws Exception
    {
        String username = (String)session.getAttribute("username");
        User user = users.findByUsername(username);
        File dir = new File("public/" + VertoSwapController.PHOTOS_DIR);
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

    //deletes a photo from the database and removes the photo from the /photo directory
    @RequestMapping(path = "/photo-delete", method = RequestMethod.POST)
    public String deletePhoto(HttpSession session, int id)
    {
        String username = (String) session.getAttribute("username");
        User user = users.findByUsername(username);
        Photo photo = photos.findOne(id);
        photos.delete(id);
        File delFile = new File("public/" + VertoSwapController.PHOTOS_DIR, photo.getFilename());
        delFile.delete();
        session.setAttribute("username", user.getUsername());
        return "redirect:/";
    }

}
