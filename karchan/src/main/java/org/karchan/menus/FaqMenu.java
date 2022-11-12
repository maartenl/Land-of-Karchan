package org.karchan.menus;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mmud.database.entities.web.Faq;

public class FaqMenu extends Menu
{
  FaqMenu(String name, String url)
  {
    super(name, url);
  }

  @Override
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    TypedQuery<Faq> faqQuery = entityManager.createNamedQuery("Faq.findAll", Faq.class);
    List<Faq> faq = faqQuery.getResultList();
    root.put("faq", faq);
  }
}
