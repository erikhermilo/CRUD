/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.modules.simple.dom.impl;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import javax.ws.rs.core.Response;

import com.google.common.collect.ComparisonChain;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;


import lombok.AccessLevel;
import org.apache.isis.core.webapp.routing.RedirectServlet;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
//print
import sun.print.PSPrinterJob;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import java.awt.print.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Date;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE, schema = "simple")
@javax.jdo.annotations.DatastoreIdentity(strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY, column="id")
@javax.jdo.annotations.Version(strategy= VersionStrategy.DATE_TIME, column="version")
@javax.jdo.annotations.Unique(name="SimpleObject_name_UNQ", members = {"name"})
@DomainObject(auditing = Auditing.ENABLED)
@DomainObjectLayout()  // causes UI events to be triggered
@lombok.Getter @lombok.Setter
@lombok.RequiredArgsConstructor
public class SimpleObject implements Comparable<SimpleObject> {

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @lombok.NonNull
    @Property() // editing disabled by default, see isis.properties
    @Title(prepend = "Nombre: ")
    private String name;

    @javax.jdo.annotations.Column(allowsNull="false", length = 20)
    @Property()
    @lombok.Getter @lombok.Setter
    @Title(prepend = " ")
    private String apaterno;

    @javax.jdo.annotations.Column(allowsNull="false", length = 20)
    @Property()
    @lombok.Getter @lombok.Setter
    @Title(prepend = " ")
    private String amaterno;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Property()
    @lombok.Getter @lombok.Setter
    @Title(prepend = " ")
    private Sexos sexo;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Property()
    @lombok.Getter @lombok.Setter
    @Title(prepend = " ")
    private LocalDate fechan;





    @Action(semantics = SemanticsOf.IDEMPOTENT, command = CommandReification.ENABLED, publishing = Publishing.ENABLED, associateWith = "fechan")
    public SimpleObject updateName(
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "Nombre")
            final String name,
            @Parameter(maxLength = 20)
            @ParameterLayout(named = "Apellido paterno")
            final String apaterno,
            @Parameter(maxLength = 20)
            @ParameterLayout(named = "Apellido materno")
            final String amaterno,
            @ParameterLayout(named = "Sexo")
            final Sexos sexo,
            @ParameterLayout(named = "Fecha de nacimiento")
            final LocalDate fechan) {
        setName(name);
        setApaterno(apaterno);
        setAmaterno(amaterno);
        setSexo(sexo);
        setFechan(fechan);
        return this;
    }

    public String default0UpdateName() {
        return getName();
    }

    public TranslatableString validate0UpdateName(final String name) {
        return name != null && name.contains("!") ? TranslatableString.tr("No se permiten simbolos") : null;
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.remove(this);
    }



    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @MemberOrder(name ="reporte",sequence ="1" )
    public java.net.URL reporte(){
        final PDPage singlePage = new PDPage();
        final PDFont courierBoldFont = PDType1Font.TIMES_BOLD;
        final int fontSize = 12;
        PDPageContentStream contentStream;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try(final PDDocument document = new PDDocument()){
            document.addPage(singlePage);
            contentStream = new PDPageContentStream(document, singlePage);
            contentStream.beginText();
            contentStream.setFont(courierBoldFont, fontSize);
            contentStream.newLineAtOffset(150,750);
            contentStream.setLeading(14.5f);
            contentStream.showText("Nombre:" + this.name);
            contentStream.newLine();
            contentStream.showText("Apellido Paterno:" + this.apaterno);
            contentStream.newLine();
            contentStream.showText(" Apellido Materno:" + this.amaterno);
            contentStream.newLine();
            contentStream.showText("Sexo:" + this.sexo);
            contentStream.newLine();
            contentStream.showText("Fecha Nacimiento:" + this.fechan);
            contentStream.newLine();

            contentStream.endText();
            PDImageXObject pdimage = PDImageXObject.createFromFile("apache-wicket.png",document);
            contentStream.drawImage(pdimage,10,5,50,50);
            contentStream.close();



            final ByteArrayOutputStream target = new ByteArrayOutputStream();

            final String name2 = "Pdf-" + this.getName() + ".pdf";
            final String mimeType = "application/pdf";
            document.save(name2);

            java.net.URL url = new java.net.URL("http://localhost:8080/DownloadFileServlet?filename="+ name2);

            return url;
        }catch (IOException ioEx){
                return null;
        }

    }


    //region > toString, compareTo
    @Override
    public String toString() {
        return getName();
    }

    public int compareTo(final SimpleObject other) {
        return ComparisonChain.start()
                .compare(this.getName(), other.getName())
                .result();
    }
    //endregion


    //region > injected services
    @javax.inject.Inject
    @javax.jdo.annotations.NotPersistent
    @lombok.Getter(AccessLevel.NONE) @lombok.Setter(AccessLevel.NONE)
    RepositoryService repositoryService;

    @javax.inject.Inject
    @javax.jdo.annotations.NotPersistent
    @lombok.Getter(AccessLevel.NONE) @lombok.Setter(AccessLevel.NONE)
    TitleService titleService;

    @javax.inject.Inject
    @javax.jdo.annotations.NotPersistent
    @lombok.Getter(AccessLevel.NONE) @lombok.Setter(AccessLevel.NONE)
    MessageService messageService;
    //endregion



}