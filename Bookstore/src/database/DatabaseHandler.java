package database;

import models.*;

import java.sql.Date;
import java.util.List;

public interface DatabaseHandler {
    boolean signUp(User user);

    boolean login(String username,String password);

    void logout();

    boolean addNewBook(Book book);

    boolean updateBookData(Book book);

    boolean UpdateUserData();

    boolean orderFromSupplier(String isbn, int quantity);

    boolean confirmOrder(LibraryOrders order);

    boolean promoteUser(String username);

    List<User> getTop5Customers();

    List<Book> viewTopSellingBooks();

    List<Book> findBook(BookDAO book);

    void addToShoppingCard(String isbn,int quantity);

    List<UserOrders> ShowShoppingCardInfo();

    boolean removeFromShoppingCard(String isbn);

    boolean Checkout();
    List<LibraryOrders> getOrders();


}
