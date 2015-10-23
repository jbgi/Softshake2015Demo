package com.github.jbgi.softshake.domain.model;

import fj.F;
import fj.Unit;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Validation;
import fj.data.optic.Prism;

import static com.github.jbgi.softshake.domain.model.Addresses.Address;
import static com.github.jbgi.softshake.domain.model.Addresses.modNumber;
import static com.github.jbgi.softshake.domain.model.Contacts.getPostalAddress;
import static com.github.jbgi.softshake.domain.model.Contacts.modPostalAddress;
import static com.github.jbgi.softshake.domain.model.FirstNames.FirstName;
import static com.github.jbgi.softshake.domain.model.LastNames.LastName;
import static com.github.jbgi.softshake.domain.model.PersonName.NameValue.parseName;
import static com.github.jbgi.softshake.domain.model.PersonNames.PersonName;
import static com.github.jbgi.softshake.domain.model.Persons.Person;
import static com.github.jbgi.softshake.domain.model.Persons.*;
import static fj.Function.identity;
import static fj.Semigroup.nonEmptyListSemigroup;
import static fj.Unit.unit;
import static fj.data.Option.*;
import static fj.data.Validation.success;
import static fj.data.optic.Prism.prism;

public class PersonService {


  public static final Prism<Option<String>, String> _some = prism(identity(), some_());

  public static void main(String[] args) {

    String stringFirstName = "Joe ";

    String stringLastName = " Black";

    System.out.println(updatePersonName(42, stringFirstName, "", stringLastName));

    // oups! there was a off my one error in the import process. We must increment all street numbers!!

    // Easy with Derive4J
    F<Person, Person> incrementStreetNumber = modContact(modPostalAddress(modNumber(number -> number + 1)));

    // correctedJoe is a copy of joe with the street number incremented:
    Person correctedJoe = findById(42).map(incrementStreetNumber).success();

    Option<Integer> newStreetNumber = getPostalAddress(getContact(correctedJoe)).map(Addresses::getNumber);

    System.out.println(newStreetNumber); // print "Optional[11]" !!
  }

  public static List<String> updatePersonName(long personId, String stringFirstName, String stringMiddleName, String stringLastName) {
    return
        // Validate input:
        validatePersonName(stringFirstName, stringMiddleName.isEmpty() ? none() : some(stringMiddleName), stringLastName).bind(
            // try to retrieve person from store
            newName -> findById(personId)
                // try to apply the command:
                .bind(person -> person.changeName(newName))
                // try to save the updated person
                .bind(updatedPerson -> savePerson(personId, updatedPerson)).nel())
            // return an emply list if no error or the list of errors:
            .validation(NonEmptyList::toList, u -> List.nil());
  }

  private static Validation<String, Unit> savePerson(long personId, Person person) {
    // actually save in your datastore...
    return success(unit());
  }

  private static Validation<String, Person> findById(long personId) {
    return success(Person(
        PersonName(
            FirstName(parseName("Joe").some()),
            none(),
            LastName(parseName("Black").some())
        ),
        Contacts.byMail(
            Address(10, "Main St")
        ),
        none()
    ));
  }

  private static Validation<NonEmptyList<String>, PersonName> validatePersonName(
      String stringFirstName, Option<String> stringMiddleName, String stringLastName) {

    return
        // validate first name
        validateName(stringFirstName, "First name").map(FirstNames::FirstName).nel().accumulate(nonEmptyListSemigroup(),

            // validate middle name if present
            stringMiddleName.map(s -> validateName(s, "Middle Name").map(MiddleNames::MiddleName).map(some_()))
                .orSome(Validation.<String, Option<PersonName.MiddleName>>success(none())).nel(),

            // validate last name
            validateName(stringLastName, "Last name").map(LastNames::LastName).nel(),

            // assemble all
            PersonNames::PersonName
        );
  }

  public static Validation<String, PersonName.NameValue> validateName(String name, String format) {
    return parseName(name).toValidation(format + " is not valid");
  }


}
