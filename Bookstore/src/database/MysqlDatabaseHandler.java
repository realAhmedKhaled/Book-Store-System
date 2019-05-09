package database;

import controllers.ShoppingCart;
import models.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MysqlDatabaseHandler implements DatabaseHandler {
    private SessionFactory factory;

    public MysqlDatabaseHandler() {
        // create session factory
        factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Book.class)
                .addAnnotatedClass(BookAuthors.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(LibraryOrders.class)
                .addAnnotatedClass(Publisher.class)
                .addAnnotatedClass(PublisherAddresses.class)
                .addAnnotatedClass(PublisherPhones.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(UserOrders.class)
                .buildSessionFactory();

        // create session
    }

    @Override
    public boolean signUp(User user) {
        Session session = factory.getCurrentSession();

        session.beginTransaction();
        session.save(user);
        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean login(String username, String password) {
        Session session = factory.getCurrentSession();
        String query = "From User u where u.pk.userName= '" + username + "'";
        session.beginTransaction();
        try {
            User user = (User) session.createQuery(query).getResultList().get(0);
            session.getTransaction().commit();
            if(user.getPassword().equals(password)){
                LoggedUser loggedUser=LoggedUser.getInstance();
                loggedUser.setUser(user);
                loggedUser.setCart(new ShoppingCart());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void logout() {

    }

    @Override
    public boolean addNewBook(Book book) {
        User user=LoggedUser.getInstance().getUser();
        if (!user.getIsManger())
            return false;
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        session.save(book);
        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateBookData(Book newBook) {
        User user=LoggedUser.getInstance().getUser();
        if(!user.getIsManger())
            return false;
        Session session = factory.getCurrentSession();

        session.beginTransaction();
        session.update(newBook);
        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean UpdateUserData() {
        User user=LoggedUser.getInstance().getUser();
        Session session = factory.getCurrentSession();

        session.beginTransaction();
        session.update(user);
        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public int orderFromSupplier(Book book, int quantity) {
        return 0;
    }

    @Override
    public boolean confirmOrder(UserOrders order) {
        return false;
    }

    @Override
    public boolean promoteUser(String username) {
        User manager=LoggedUser.getInstance().getUser();
        Session session = factory.getCurrentSession();
        if (manager.getIsManger()) {
            String query = "From User u where u.pk.userName= '" +username + "'";
            System.out.println(query);
            session.beginTransaction();
            User user = (User) session.createQuery(query).getResultList().get(0);
           try {
               session.getTransaction().commit();
               user.setIsManger(true);
               session = factory.getCurrentSession();
               session.beginTransaction();
               session.update(user);
               session.getTransaction().commit();
               return true;
           }catch (Exception e)
           {
               e.printStackTrace();
               return false;
           }

        }
        return false;
    }

    @Override
    public List<User> getTop5Customers() {
        return null;
    }

    @Override
    public List<Book> viewTopSellingBooks() {
        return null;
    }

    @Override
    public List<Book> findBook(BookDAO bookData) {
        Session session = factory.getCurrentSession();
        StringBuilder query =new StringBuilder( "From Book b where");
        boolean flag=false;
        if(bookData.getIsbn()!=null)
        {
            query.append(" b.isbn like '");
            query.append(bookData.getIsbn());
            query.append("%'");
            flag=true;
        }
        if(bookData.getTitle()!=null)
        {
            if(flag)
                query.append(" and ");
            query.append("b.title like '");
            query.append(bookData.getTitle());
            query.append("%'");
            flag=true;
        }
        if(bookData.getLowerPrice()!=0)
        {
            if(flag)
                query.append(" and ");
            query.append(" b.price >= ");
            query.append(bookData.getLowerPrice());
            flag=true;
        }
        if(bookData.getUpperPrice()!=0)
        {
            if(flag)
                query.append(" and ");
            query.append(" b.price <= ");
            query.append(bookData.getUpperPrice());
        }

        System.out.println(query.toString());
        session.beginTransaction();
        try {
            List<?> books =  session.createQuery(query.toString()).getResultList();
            session.getTransaction().commit();

            return (List<Book>)books;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public boolean addToShoppingCard(User user, Book book, int quantity) {
        Session session = factory.getCurrentSession();

        UserOrders order = new UserOrders(book.getIsbn(), user.getUserName(), user.getEmail(), quantity);
        session.beginTransaction();
        session.save(order);
        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Book> ShowShoppingCardInfo(User user) {
        Session session = factory.getCurrentSession();

        String query = "From UserOrders u where u.pk.userName= '" + user.getUserName() + "' and u.pk.email='" + user.getEmail() + "'";
        session.beginTransaction();
        List<?> orders = session.createQuery(query).getResultList();
        List<Book> books = new ArrayList<>();
        session.getTransaction().commit();
        for (Object o : orders) {
            session = factory.getCurrentSession();

            session.beginTransaction();
            Book b = session.get(Book.class, ((UserOrders) o).getIsbn());
            b.setQuantity(((UserOrders) o).getQuantity());
            books.add(b);
            session.getTransaction().commit();
        }

        return books;
    }

    @Override
    public boolean removeShoppingCard(User user) {
        return false;
    }

    @Override
    public boolean Checkout(User user, String creditCard, Date expireDate) {
        return false;
    }
}
