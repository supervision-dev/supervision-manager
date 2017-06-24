package com.rmbank.supervision.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class XmlUtil
{
  public static final int NODE_SHEME = 1;
  public static final int ATTRIBUTE_SHEME = 2;
  public static final int SIMPLE_NODE = 1;
  public static final int PACKAGE_NODE = 2;
  protected final Log log = LogFactory.getLog(getClass());

  public Document StringToXml(String xmlStr)
  {
    try
    {
      if ((xmlStr == null) || ("".equals(xmlStr))) {
        return null;
      }
      return DocumentHelper.parseText(xmlStr);
    }
    catch (Exception e) {
      this.log.error(e.getMessage());
    }return null;
  }

  public String XmlToString(Document doc)
  {
    if (doc == null) {
      return null;
    }
    return doc.asXML();
  }

  public Document ObjectToXml(String rootName, Object object, int scheme, int nodeType)
  {
    if (object == null) {
      return null;
    }
    Document doc = new DOMDocument();
    Element root = new DOMElement(rootName);
    doc.add(root);
    Iterator localIterator;
    if ((object instanceof List))
      for (localIterator = ((List)object).iterator(); localIterator.hasNext(); ) { Object obj = localIterator.next();
        findObject(root, obj, scheme, nodeType);
      }
    else if ((object instanceof Set))
      for (localIterator = ((Set)object).iterator(); localIterator.hasNext(); ) { Object obj = localIterator.next();
        findObject(root, obj, scheme, nodeType);
      }
    else {
      findObject(root, object, scheme, nodeType);
    }
    return doc;
  }

  protected void findObject(Element root, Object object, int scheme, int nodeType)
  {
    Class clazz = object.getClass();

    Field[] fields = clazz.getDeclaredFields();
    Element element = null;
    if (nodeType == 1)
      element = new DOMElement(
        firstUpperToLetter(clazz.getSimpleName()));
    else {
      element = new DOMElement(clazz.getName());
    }
    if (scheme == 2) {
      for (Field field : fields) {
        parseChildren(field, element, object, nodeType);
      }
    }
    else {
      for (Field field : fields) {
        Element felement = new DOMElement(field.getName());
        felement.setText(toString(getter(object, 
          firstLetterToUpper(field.getName()))));
        element.add(felement);
      }
    }
    root.add(element);
  }

  protected void parseChildren(Field field, Element element, Object object, int nodeType)
  {
    Class type = field.getType();
    if (isSimpleType(type))
      element.setAttributeValue(
        field.getName(), 
        toString(getter(object, firstLetterToUpper(field.getName()))));
    else
      try {
        Class clazz = Class.forName(field.getType()
          .getCanonicalName());
        Object child = getter(object, 
          firstLetterToUpper(field.getName()));
        if (child != null) {
          Element chidElelment = null;
          if (nodeType == 1)
            chidElelment = new DOMElement(
              firstUpperToLetter(clazz.getSimpleName()));
          else {
            chidElelment = new DOMElement(clazz.getName());
          }
          element.add(chidElelment);
          Field[] chidFields = clazz.getDeclaredFields();
          for (Field chidField : chidFields)
            parseChildren(chidField, chidElelment, child, nodeType);
        }
      } catch (Exception e) {
        this.log.error(e.getMessage());
      }
  }

  public Object XmlToObject(Document doc, int scheme)
  {
    Object object = null;
    try {
      Element root = doc.getRootElement();

      Iterator iters = root.elementIterator();
      object = findObject(iters);
    } catch (Exception e) {
      this.log.error(e.getMessage());
    }
    return object;
  }

  protected Object findObject(Iterator<Element> iters) {
    try {
      Object object = null;
      if (iters.hasNext()) {
        Element item = (Element)iters.next();
        if (item.getName().indexOf(".") != -1) {
          Class clazz = Class.forName(item.getName());
          object = clazz.newInstance();
          Field[] fields = clazz.getDeclaredFields();
          for (Field field : fields) {
            if (isSimpleType(field.getType())) {
              setter(object, firstLetterToUpper(field.getName()), 
                item.attribute(field.getName()).getValue(), 
                field.getType());
            }
            else {
              Iterator children = item.elementIterator();
              Object o = findObject(children);
              if (o.getClass().getName().equals(field.getType().getCanonicalName())) {
                setter(object, firstLetterToUpper(field.getName()), o, field.getType());
              }
            }
          }
        }
        return object;
      }
    }
    catch (Exception e) {
      this.log.error(e.getMessage());
    }
    return null;
  }

  protected boolean isSimpleType(Class<?> type)
  {
    if ((type == String.class) || 
      (type == Integer.TYPE) || (type == Integer.class) || 
      (type == Double.TYPE) || (type == Double.class) || 
      (type == Character.TYPE) || (type == Character.class) || 
      (type == Long.TYPE) || (type == Long.class) || 
      (type == Float.TYPE) || (type == Float.class) || 
      (type == Byte.TYPE) || (type == Byte.class) || 
      (type == Boolean.TYPE) || (type == Boolean.class) || 
      (type == Short.TYPE) || (type == Short.class)) {
      return true;
    }
    return false;
  }

  protected Object getter(Object obj, String att)
  {
    try
    {
      Method method = obj.getClass().getMethod("get" + att, new Class[0]);
      return method.invoke(obj, new Object[0]);
    } catch (Exception e) {
      this.log.error(e.getMessage());
    }return null;
  }

  protected void setter(Object obj, String att, Object value, Class<?> type)
  {
    try
    {
      Method method = obj.getClass().getMethod("set" + att, new Class[] { type });
      if (type == String.class)
        method.invoke(obj, new Object[] { toString(value) });
      else if ((type == Integer.class) || (type == Integer.TYPE))
        method.invoke(obj, new Object[] { toInteger(value) });
      else if ((type == Double.TYPE) || (type == Double.class))
        method.invoke(obj, new Object[] { toDouble(value) });
      else if ((type == Character.TYPE) || (type == Character.class))
        method.invoke(obj, new Object[] { toCharacter(value) });
      else if ((type == Long.TYPE) || (type == Long.class))
        method.invoke(obj, new Object[] { toLong(value) });
      else if ((type == Float.TYPE) || (type == Float.class))
        method.invoke(obj, new Object[] { toFloat(value) });
      else if ((type == Byte.TYPE) || (type == Byte.class))
        method.invoke(obj, new Object[] { toByte(value) });
      else if ((type == Boolean.TYPE) || (type == Boolean.class))
        method.invoke(obj, new Object[] { toBoolean(value) });
      else if ((type == Short.TYPE) || (type == Short.class))
        method.invoke(obj, new Object[] { toShort(value) });
      else
        method.invoke(obj, new Object[] { value });
    } catch (Exception e) {
      this.log.error(e.getMessage());
    }
  }

  protected String firstLetterToUpper(String str)
  {
    char[] array = str.toCharArray();
    int tmp7_6 = 0;
    char[] tmp7_5 = array; tmp7_5[tmp7_6] = ((char)(tmp7_5[tmp7_6] - ' '));
    return String.valueOf(array);
  }

  protected String firstUpperToLetter(String str)
  {
    char[] array = str.toCharArray();
    int tmp7_6 = 0;
    char[] tmp7_5 = array; tmp7_5[tmp7_6] = ((char)(tmp7_5[tmp7_6] + ' '));
    return String.valueOf(array);
  }

  protected String toString(Object object)
  {
    if (object == null) {
      return "";
    }
    return object.toString();
  }

  protected Integer toInteger(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Integer.valueOf(0);
    }
    return Integer.valueOf(Integer.parseInt(str));
  }

  protected Double toDouble(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Double.valueOf(0.0D);
    }
    return Double.valueOf(Double.parseDouble(str));
  }

  protected Float toFloat(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Float.valueOf(0.0F);
    }
    return Float.valueOf(Float.parseFloat(str));
  }

  protected Long toLong(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Long.valueOf(0L);
    }
    return Long.valueOf(Long.parseLong(str));
  }

  protected Boolean toBoolean(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Boolean.valueOf(true);
    }
    return Boolean.valueOf(Boolean.parseBoolean(str));
  }

  protected Short toShort(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Short.valueOf((short)0);
    }
    return Short.valueOf(Short.parseShort(str));
  }

  protected Byte toByte(Object object)
  {
    String str = toString(object);
    if ("".equals(str)) {
      return Byte.valueOf((byte)0);
    }
    return Byte.valueOf(Byte.parseByte(str));
  }

  protected Character toCharacter(Object object)
  {
    if (object == null) {
      return Character.valueOf('௫');
    }
    return (Character)object;
  }
}