package com.lambdaschool.bookstore.services;

import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.exceptions.ResourceNotFoundException;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookstoreApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//**********
// Note security is handled at the controller, hence we do not need to worry about security here!
//**********
public class BookServiceImplTest
{

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private SectionService sectionService;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);

        // getting book id
//        List<Book> myList = bookService.findAll();
//        for (Book b : myList)
//        {
//            System.out.println(b.getBookid() + " " + b.getTitle());
//        }

//        26 Flatterland
//        27 Digital Fortess
//        28 The Da Vinci Code
//        29 Essentials of Finance
//        30 Calling Texas Home

    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void a_findAll()
    {
        assertEquals(5, bookService.findAll().size());
    }

    @Test
    public void b_findBookById()
    {
        assertEquals("The Da Vinci Code",bookService.findBookById(28).getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void c_notFindBookById()
    {
        assertEquals("The Turtle Book",bookService.findBookById(1000).getTitle());
    }

    @Test
    public void y_delete()
    {
        bookService.delete(30);
        assertEquals(5, bookService.findAll().size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void x_notFoundDelete()
    {
        bookService.delete(10000);
        assertEquals(5, bookService.findAll()
                .size());
    }

    @Test
    public void d_save()
    {
        // author
        Author a7 = new Author("Christian", "Bautista");
        a7 = authorService.save(a7);

        // section
        Section s6 = new Section("Turtle");
        s6 = sectionService.save(s6);

        // new book
        Book b1 = new Book("Java Spring through the Wisdom of the Turtle", "9780738206752", 2020, s6);
        b1.getWrotes().add(new Wrote(a7, new Book()));
        Book newBook = bookService.save(b1);

        assertNotNull(newBook);
        assertEquals("Java Spring through the Wisdom of the Turtle",newBook.getTitle());

    }

    @Test(expected = ResourceNotFoundException.class)
    public void e_saveNoAuthorIdFound()
    {
        Book b = new Book();
        b.setCopy(new Random().nextInt(100));
        b.setIsbn(UUID.randomUUID().toString());
        b.setTitle("TEST");
        var sections = sectionService.findAll();
        var authors = authorService.findAll();
        var aToSave = authors.get(new Random().nextInt(authors.size()));
        aToSave.setAuthorid(1000);
        b.setSection(sections.get(new Random().nextInt(sections.size())));
        b.getWrotes().add(new Wrote(aToSave, b));
        var savedBook = bookService.save(b);
        assertTrue(savedBook.getBookid() > 0);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void f_saveNoIdFound()
    {
        Book b = new Book();
        b.setCopy(new Random().nextInt(100));
        b.setIsbn(UUID.randomUUID().toString());
        b.setTitle("TEST");
        b.setBookid(1000);
        var sections = sectionService.findAll();
        var authors = authorService.findAll();
        b.setSection(sections.get(new Random().nextInt(sections.size())));
        b.getWrotes().add(new Wrote(authors.get(new Random().nextInt(authors.size())), b));
        var savedBook = bookService.save(b);
        assertTrue(savedBook.getBookid() > 0);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void g_saveputfailed()
    {
        Author a7 = new Author("Christian", "Bautista");
        a7.setAuthorid(20);
        Section s6 = new Section("Turtle");
        s6.setSectionid(201);

        Book b5 = new Book("The Turtle Book", "021099103910", 2000, s6);
        b5.getWrotes()
                .add(new Wrote(a7, new Book()));
        Book saveB5 = bookService.save(b5);
        assertEquals("The Turtle Book", saveB5.getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void r_saveputfailed()
    {
        Author a7 = new Author("Christian", "Bautista");
        a7.setAuthorid(20);
        Section s6 = new Section("Fitness");
        s6.setSectionid(21);

        Book b5 = new Book("The Turtle Book", "021099103910", 2000, s6);
        b5.getWrotes()
                .add(new Wrote(a7, new Book()));
        b5.setBookid(500);
        Book saveB5 = bookService.save(b5);
        assertEquals("The Turtle Book", saveB5.getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void s_savePutFailedAuthor()
    {
        Author a7 = new Author("Christian", "Bautista");
        Section s6 = new Section("Turtle");
        s6.setSectionid(21);

        Book b5 = new Book("The Turtle Book", "021099103910", 2000, s6);
        b5.getWrotes()
                .add(new Wrote(a7, new Book()));
        b5.setBookid(500);
        b5.setCopy(300);
        Book saveB5 = bookService.save(b5);
        assertEquals("The Turtle Book", saveB5.getTitle());
    }

    @Transactional
    @Test
    public void w_update()
    {

        // author
        Author a7 = new Author("Christian", "Bautista");
        a7.setAuthorid(20);

        // section
        Section s6 = new Section("Turtle");
        s6.setSectionid(21);

        // new book
        Book b1 = new Book("Updated Title", "9780738206752", 2020, s6);
        b1.getWrotes().add(new Wrote(a7, new Book()));
        Book updatedBook = bookService.save(b1);

        assertEquals("Updated Title", updatedBook.getTitle());
    }


    @Transactional
    @Test(expected = ResourceNotFoundException.class)
    public void x_updateFailed()
    {

        // author
        Author a7 = new Author("Christian", "Bautista");
        a7.setAuthorid(1000);

        // section
        Section s6 = new Section("Turtle");
        s6.setSectionid(10000);

        // new book
        Book b1 = new Book("Updated Title", "9780738206752", 2020, s6);
        b1.getWrotes().add(new Wrote(a7, new Book()));
        Book updatedBook = bookService.save(b1);

        assertEquals("Updated Title", updatedBook.getTitle());
    }

    @Test
    public void  xxx_update() {
        Author a6 = new Author("Ian", "Stewart");
        a6.setAuthorid(20);
        Section s1 = new Section("Fiction");
        s1.setSectionid(21);
        Book b1 = new Book("ButterLand", "9780738206752", 2001, s1);
        b1.getWrotes()
                .add(new Wrote(a6, new Book()));
        b1.setCopy(100);
        Book newB1 = bookService.update(b1,26);
        assertEquals("ButterLand", newB1.getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void  xx_updateFailed() {
        Author a6 = new Author("Ian", "Stewart");
        a6.setAuthorid(20450);
        Section s1 = new Section("Fiction");
        s1.setSectionid(21);
        Book b1 = new Book("ButterLand", "9780738206752", 2001, s1);
        b1.getWrotes()
                .add(new Wrote(a6, new Book()));
        b1.setCopy(100);
        Book newB1 = bookService.update(b1,26);
        assertEquals("ButterLand", newB1.getTitle());
    }

    @Transactional
    @Test
    public void z_deleteAll()
    {
        bookService.deleteAll();
        assertEquals(0,bookService.findAll().size());
    }
}