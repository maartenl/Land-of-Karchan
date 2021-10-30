package org.karchan.menus;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import mmud.database.entities.web.Blog;

public class WelcomeMenu extends Menu
{
  private static final int MAX_LATEST_BLOGS = 5;

  public WelcomeMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    TypedQuery<Blog> blogsQuery = entityManager.createNamedQuery("Blog.findAll", Blog.class);
    blogsQuery.setMaxResults(MAX_LATEST_BLOGS);
    List<Blog> blogs = blogsQuery.getResultList();
    root.put("blogs", blogs);
  }
}
