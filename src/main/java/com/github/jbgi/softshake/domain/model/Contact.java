package com.github.jbgi.softshake.domain.model;

import org.derive4j.Data;
import org.derive4j.Flavour;

@Data(flavour = Flavour.FJ)
public abstract class Contact {

  public abstract <R> R match(Cases<R> cases);

  interface Cases<R> {
    R byEmail(String email);

    R byPhone(String phoneNumber);

    R byMail(Address postalAddress);
  }

}
