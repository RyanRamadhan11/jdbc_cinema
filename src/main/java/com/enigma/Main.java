package com.enigma;


import com.enigma.entity.*;
import com.enigma.repository.*;
import com.enigma.repository.impl.*;

public class Main {
    public static void main(String[] args) {

        CustomerRepo customerRepo = new CustomerRepoImpl();

//        System.out.println(customerRepo.getAll());

//        customerRepo.addCustomer(new Customer("Ryan", "2000-01-01"));

//        customerRepo.updateCustomer(new Customer(18,"Joo Ryan","2005-04-04"));

//        customerRepo.deleteCustomer(16);

//        System.out.println(customerRepo.getCustomerById(13));


        RatingRepo ratingRepo = new RatingRepoImpl();

//        System.out.println(ratingRepo.getAll());

//        ratingRepo.addRating(new Rating("tes", "semua bebas"));

//        ratingRepo.updateRating(new Rating(6, "Z", "Tidak Boleh"));

//        ratingRepo.deleteRating(6);

//        System.out.println(ratingRepo.getRatingById(4));


        FilmRepo filmRepo = new FilmRepoImpl();

//        System.out.println(filmRepo.getAll());

//        filmRepo.addFilm(new Film("Si Joo 2", 60, "2023-12-01",60000,3));

//        filmRepo.updateFilm(new Film(16, "Horor", 80, "2023-12-12", 25000, 2));

//        filmRepo.deleteFilm(16);

//        System.out.println(filmRepo.getFilmById(11));


        TheaterRepo theaterRepo = new TheaterRepoImpl();

//        System.out.println(theaterRepo.getAll());

//        theaterRepo.addTheater(new Theater("T012", 40, 1));

//        theaterRepo.updateTheater(new Theater(12, "T013", 45, 2));

//        theaterRepo.deleteTheater(12);

//        System.out.println(theaterRepo.getTheaterById(10));


        ChairRepo chairRepo = new ChairRepoImpl();

//        System.out.println(chairRepo.getAll());

//        chairRepo.addChair(new Chair("Z09",1));

//        chairRepo.updateChair(new Chair(56, "Z01", 2));

//        chairRepo.deleteChair(55);

//        System.out.println(chairRepo.getChairById(1));



        TicketRepo ticketRepo = new TicketRepoImpl();

//        System.out.println(ticketRepo.getAll());

//        ticketRepo.addTicket(new Ticket(1, 6));

//        ticketRepo.updateTicket(new Ticket(4,36,1));

//        ticketRepo.deleteTicket(3);

//        System.out.println(ticketRepo.getTicketById(1));

    }

}