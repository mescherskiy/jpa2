package jpa1;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add apartment");
                    System.out.println("2: delete apartment");
                    System.out.println("3: change apartment");
                    System.out.println("4: view some apartments");
                    System.out.println("5: view all apartments");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartment(sc);
                            break;
                        case "2":
                            deleteApartment(sc);
                            break;
                        case "3":
                            changeApartment(sc);
                            break;
                        case "4":
                            viewApartments(sc);
                            break;
                        case "5":
                            viewAllApartments();
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void addApartment(Scanner sc) {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter apartment's area: ");
        String areaStr = sc.nextLine();
        double area = Double.parseDouble(areaStr);
        System.out.print("Enter number of rooms: ");
        String roomsStr = sc.nextLine();
        int rooms = Integer.parseInt(roomsStr);
        System.out.print("Enter price: ");
        String priceStr = sc.nextLine();
        int price = Integer.parseInt(priceStr);


        em.getTransaction().begin();
        try {
            Apartment a = new Apartment(district, address, area, rooms, price);
            em.persist(a);
            em.getTransaction().commit();

            System.out.println(a.getId());
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void deleteApartment(Scanner sc) {
        System.out.print("Enter apartment's id: ");
        String sId = sc.nextLine();
        long id = Long.parseLong(sId);

        Apartment a = em.getReference(Apartment.class, id);
        if (a == null) {
            System.out.println("Apartment not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(a);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void changeApartment(Scanner sc) {
        System.out.print("Enter apartment's id: ");
        String sId = sc.nextLine();
        long id = Long.parseLong(sId);

        System.out.print("Enter new price: ");
        String priceStr = sc.nextLine();
        int price = Integer.parseInt(priceStr);

        Apartment a = null;
        try {
            Query query = em.createQuery(
                    "SELECT x FROM Apartment x WHERE x.id = :id", Apartment.class);
            query.setParameter("id", id);
            a = (Apartment) query.getSingleResult();
        } catch (NoResultException ex) {
            System.out.println("Apartment not found!");
            return;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }

        ///........

        em.getTransaction().begin();
        try {
            a.setPrice(price);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewApartments(Scanner sc) {
        System.out.println("Choose a search parameter:" + System.lineSeparator());
        System.out.println("1. District");
        System.out.println("2. Address");
        System.out.println("3. Area");
        System.out.println("4. Number of rooms");
        System.out.println("5. Price");
        System.out.print("-> ");
        String p = sc.nextLine();
        String whereQuery = "";
        switch (p) {
            case "1":
                whereQuery += "district";
                break;
            case "2":
                whereQuery += "address";
                break;
            case "3":
                whereQuery += "area";
                break;
            case "4":
                whereQuery += "rooms";
                break;
            case "5":
                whereQuery += "price";
                break;
            default:
                return;
        }
        System.out.println("Enter the value to search:");
        System.out.print("-> ");
        String value = "'" + sc.nextLine() + "'";

        Query query = em.createQuery(
                "SELECT a FROM Apartment a WHERE a." + whereQuery + "=" + value, Apartment.class);
        List<Apartment> list = (List<Apartment>) query.getResultList();

        for (Apartment a : list)
            System.out.println(a);
    }

    private static void viewAllApartments() {
        Query query = em.createQuery(
                "SELECT a FROM Apartment a", Apartment.class);
        List<Apartment> list = (List<Apartment>) query.getResultList();

        for (Apartment a : list)
            System.out.println(a);
    }
}


