/*
 * Copyright (C) 2014 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.testing.tests;

import jakarta.persistence.EntityManager;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Room;
import mmud.services.LogService;
import mmud.testing.TestingConstants;
import org.testng.annotations.BeforeMethod;

/**
 * @author maartenl
 */
public class LogServiceTest extends MudTest
{

    private Administrator karn;
    private User marvin;


    private EntityManager entityManager;

    private final LogService logService = new LogService()
    {

        @Override
        protected EntityManager getEntityManager()
        {
            return entityManager;
        }
    };

    public LogServiceTest()
    {
    }

    @BeforeMethod
    public void setup()
    {
    }
//
//    @Test
//    public void testWriteLogSuperSimple()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isNull();
//                        assertThat(log.getMessage()).isEqualTo("Hello, world.");
//                        assertThat(log.getAddendum()).isNull();
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//
//            }
//        };
//        logBean.writeLog("Hello, world.");
//    }
//
//    @Test
//    public void testWriteLogSuperSimpleWithAddendum()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isNull();
//                        assertThat(log.getMessage()).isEqualTo("Hello, world.");
//                        assertThat(log.getAddendum()).isEqualTo("And that means: You!");
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//
//            }
//        };
//        logBean.writeLog("Hello, world.", "And that means: You!");
//    }
//
//    @Test
//    public void testWriteLogSimple()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        assertThat(log.getMessage()).isEqualTo("Hello, world.");
//                        assertThat(log.getAddendum()).isNull();
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//
//            }
//        };
//        logBean.writeLog(karn, "Hello, world.");
//    }
//
//    @Test
//    public void testWriteLogSimpleWithAddendum()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        assertThat(log.getMessage()).isEqualTo("Hello, world.");
//                        assertThat(log.getAddendum()).isEqualTo("And I mean earth!");
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//
//            }
//        };
//        logBean.writeLog(karn, "Hello, world.", "And I mean earth!");
//    }
//
//    @Test
//    public void testWriteLogMaximum()
//    {
//        final String longLogMessage = "Hello, world. Karn here. This is a test to determine if 255 characters actually just about fits.lllllslllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll";
//
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        assertThat(log.getMessage()).isEqualTo(longLogMessage);
//                        assertThat(log.getAddendum()).isNull();
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//
//            }
//        };
//        assertThat(longLogMessage.length()).isEqualTo(255);
//        logBean.writeLog(karn, longLogMessage);
//    }
//
//    @Test
//    public void testWriteLogTooLong()
//    {
//        final String longLogMessage = "Hello, world. Karnd here. This is a test to determine if 255 characters actually just about fits.lllllslllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll1234567890";
//
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        assertThat(log.getMessage()).isEqualTo("The log message was too long!");
//                        assertThat(log.getAddendum()).isEqualTo(longLogMessage);
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        assertThat(log.getMessage()).isEqualTo(longLogMessage.substring(0, 255));
//                        assertThat(log.getAddendum()).isNull();
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//            }
//        };
//        assertThat(longLogMessage.length()).isEqualTo(256);
//        logBean.writeLog(karn, longLogMessage);
//    }
//
//    @Test
//    public void testWriteCommandLog()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Commandlog) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Commandlog log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        // assertThat(log.getStamp()).isEqualTo(LocalDateTime.now()));
//                        assertThat(log.getCommand()).isEqualTo("wave");
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//            }
//        };
//        logBean.writeCommandLog(karn, "wave");
//    }
//
//    @Test
//    public void testWriteLogException()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isNull();
//                        assertThat(log.getMessage()).isEqualTo("java.lang.NullPointerException");
//                        assertThat(log.getAddendum().substring(0, 90)).isEqualTo("java.lang.NullPointerException\n\tat mmud.testing.tests.LogBeanTest.testWriteLogException(Lo");
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//            }
//        };
//        try
//        {
//            Administrator admin = new Administrator();
//            int length = admin.getName().length();
//            fail("Exception expected, but length was " + length);
//        } catch (Exception e)
//        {
//            logBean.writeLogException(e);
//        }
//    }
//
//    @Test
//    public void testWriteLogExceptionWithPerson()
//    {
//        new Expectations() // an "expectation block"
//        {
//
//            {
//                entityManager.persist((Log) any);
//                result = new Delegate()
//                {
//                    // The name of this method can actually be anything.
//                    void persist(Log log)
//                    {
//                        assertThat(log).isNotNull();
//                        assertThat(log.getName()).isEqualTo("Karn");
//                        assertThat(log.getMessage()).isEqualTo("java.lang.NullPointerException");
//                        assertThat(log.getAddendum().substring(0, 97)).isEqualTo("java.lang.NullPointerException\n\tat mmud.testing.tests.LogBeanTest.testWriteLogExceptionWithPerson");
//                        assertThat(log.getId()).isNull();
//                    }
//                };
//            }
//        };
//        try
//        {
//            Administrator admin = new Administrator();
//            int length = admin.getName().length();
//            fail("Exception expected, but length was " + length);
//        } catch (Exception e)
//        {
//            logBean.writeLogException(karn, e);
//        }
//    }


    @BeforeMethod
    public void setUpMethod() throws Exception
    {
        karn = TestingConstants.getKarn();
        final Room room = TestingConstants.getRoom(TestingConstants.getArea());
        karn.setRoom(room);
        marvin = TestingConstants.getMarvin(room);
    }

}
