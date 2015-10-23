/*
 * Copyright (c) 2015, Jean-Baptiste Giraudeau <jb@giraudeau.info>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.jbgi.softshake.domain.model;


import fj.F;
import fj.data.Option;
import fj.data.optic.Iso;
import fj.data.optic.Lens;
import fj.data.optic.Prism;
import org.derive4j.*;

import static com.github.jbgi.softshake.domain.model.NameValues.NameValue;
import static fj.Function.identity;
import static fj.data.Option.some;
import static fj.data.optic.Iso.iso;
import static fj.data.optic.Lens.lens;
import static fj.data.optic.Prism.prism;

@Data(flavour = Flavour.FJ)
public abstract class PersonName {

  public abstract <R> R match(Cases<R> cases);

  public interface Cases<R> {
    R PersonName(FirstName firstName, Option<MiddleName> middleName, LastName lastName);
  }

  /**
   * The person first name
   */
  @Data(flavour = Flavour.FJ)
  public static abstract class FirstName {
    public abstract <R> R match(@FieldNames("name") F<NameValue, R> FirstName);
  }

  /**
   * The person first name
   */
  @Data(flavour = Flavour.FJ)
  public static abstract class MiddleName {
    public abstract <R> R match(@FieldNames("name") F<NameValue, R> MiddleName);
  }

  /**
   * The person first name
   */
  @Data(flavour = Flavour.FJ)
  public static abstract class LastName {
    public abstract <R> R match(@FieldNames("name") F<NameValue, R> LastName);
  }

  /**
   * A name value, cannot be only spaces, must not start or end with space,
   * and must be at most 120 characters. construction via {@link #parseName(String)} only.
   */
  @Data(value = @Derive(withVisbility = Visibility.Smart), flavour = Flavour.FJ)
  public static abstract class NameValue {
    NameValue() {
    }

    public static Option<NameValue> parseName(String value) {
      return !value.trim().isEmpty()
          && !value.endsWith(" ")
          && !value.startsWith(" ")
          && value.length() <= 120

          ? some(NameValue(value))
          : Option.<NameValue>none();
    }

    public abstract <R> R match(@FieldNames("value") F<String, R> NameValue);
  }

}
