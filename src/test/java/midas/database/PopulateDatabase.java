/*
 * Copyright 2011-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package midas.database;

import java.util.Random;

import midas.entity.jpa.CustomerJpa;
import midas.entity.solr.CustomerSolr;
import midas.repository.jpa.CustomerJpaRepository;
import midas.repository.solr.CustomerSolrRepository;
import midas.testcategory.TryOut;

import org.dozer.Mapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author caio.amaral
 *
 */
@Category(TryOut.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/WEB-INF/applicationContext.xml" })
public class PopulateDatabase {
	private static final String[] NAMES = { "Tom", "Leaton", "Roxann", "Dolan",
			"Tricia", "Ackles", "Adelaide", "Giusti", "Lidia", "Kidder",
			"Dotty", "Monty", "Jamar", "Crowson", "Alvera", "Urbain",
			"Marleen", "Izquierdo", "Araceli", "Concannon", "Julie",
			"Lamoureux", "Tiara", "Morelock", "Bella", "Rodrigez", "Lashandra",
			"Vidrine", "Delsie", "Williams", "Reginald", "Moak", "Annika",
			"Gong", "Billy", "Shawl", "Valeria", "Oppenheimer", "Dorene",
			"Arocho", "Hoa", "Salzer", "Bridgett", "Navarro", "Nigel",
			"Schuler", "Aimee", "Rocker", "Jada", "Boroughs", "Melva",
			"Yoshimoto", "Laurence", "Chrzanowski", "Catalina", "Bartkowiak",
			"Una", "Hotard", "Marylin", "Smathers", "Hui", "Geib", "Ebonie",
			"Fouts", "Faviola", "Croghan", "Reynalda", "Koepsell", "Portia",
			"Byrge", "Catrice", "Leick", "Luisa", "Body", "Alishia", "Fonte",
			"Bunny", "Freitag", "Demetria", "Oriol", "Lorette", "Bengtson",
			"Noriko", "Lovejoy", "Latisha", "Manganaro", "Zofia", "Rosendahl",
			"Myra", "Schaal", "Chere", "Mccullah", "Traci", "Fonseca",
			"Violeta", "Sanks", "King", "Alsobrook", "Cecille", "Mccracken",
			"Kandi", "Dombrosky", "Brenda", "Baughn", "Leona", "Droz", "Norah",
			"Steffy", "Lyn", "Lauderdale", "Isaura", "Jeong", "Jaimie",
			"Hooper", "Benito", "Fine", "Camille", "Latorre", "Kathrin",
			"Towne", "Reagan", "Leatham", "Twana", "Mercure", "Amado",
			"Burfield", "Malia", "Fowler", "Wen", "Whitten", "Cherly",
			"Sparkman", "Walker", "Arthur", "Bret", "Dinapoli", "Debora",
			"Henrickson", "Sachiko", "Haygood", "Marya", "Lien", "Kelle",
			"Lapinski", "Kacie", "Arzu", "Eduardo", "Viers", "Arlyne",
			"Barbara", "Lane", "Brenes", "Florida", "Seo", "Cecelia", "Gade",
			"Carmela", "Mendelson", "Yasmin", "Ireland", "Neida", "Grinnell",
			"Evelynn", "Dail", "Juan", "Rott", "Xenia", "Colter", "Lavinia",
			"Crumbley", "Daina", "Weekly", "Leisha", "Nghiem", "Claretha",
			"Napoleon", "Martha", "Dunaway", "Alita", "Grimshaw", "Noble",
			"Mcnemar", "Cornelia", "Buchholtz", "Lauretta", "Speidel", "Ivory",
			"Dalzell", "Milda", "Lieu", "Ermelinda", "Curci", "Shery", "Dileo",
			"Ruthie", "Neblett", "Zetta", "Bowyer", "Torrie", "Vass",
			"Ladonna", "Constance", "Kesha", "Berg", "Leontine", "Locascio",
			"Danette", "Boots", "Vi", "Deloera", "Jan", "Kantner", "Adolph",
			"Dearth", "Dyan", "Condict", "Nettie", "Sharif", "Florene",
			"Westberry", "Hilary", "Coletta", "Shenika", "Dillahunty", "Allie",
			"Hermansen", "Phil", "Meeker", "Chastity", "Carballo", "Laurence",
			"Helmick", "Adela", "Packer", "Oda", "Merkel", "Simon", "Condon",
			"Rochel", "Sapienza" };

	@Autowired
	private CustomerSolrRepository customerSolrRepo;

	@Autowired
	private CustomerJpaRepository customerJpaRepo;

	@Autowired
	@Qualifier("customerMapper")
	private Mapper customerMapper;

	@Test
	public void populate() {
		Random r = new Random(System.currentTimeMillis());

		for (int i = 0; i < 100; i++) {
			int firstIdx = r.nextInt(NAMES.length);
			int middleIdx = r.nextInt(NAMES.length);
			int lastIdx = r.nextInt(NAMES.length);

			final CustomerSolr domain;
			switch (r.nextInt(3)) {
			case 0:
				domain = new CustomerSolr(i, NAMES[firstIdx], NAMES[lastIdx]);
				break;
			case 1:
				domain = new CustomerSolr(i, String.format("%s %s",
						NAMES[firstIdx], NAMES[middleIdx]), NAMES[lastIdx]);
				break;
			default:
				domain = new CustomerSolr(i, NAMES[firstIdx], String.format(
						"%s %s", NAMES[middleIdx], NAMES[lastIdx]));
			}

			final CustomerSolr document = customerMapper.map(domain,
					CustomerSolr.class);
			customerSolrRepo.save(document);

			final CustomerJpa entity = customerMapper.map(domain, CustomerJpa.class);
			customerJpaRepo.save(entity);
		}
	}

	@Test
	public void testA() {
		Pageable page = new SolrPageRequest(0, 5);
		Page<CustomerSolr> customers = customerSolrRepo.findMoreLikeThis("haygood sparkman", page);
		for (CustomerSolr customerSolr : customers) {
			System.out.println(customerSolr.getFirstName() +" " + customerSolr.getLastName());
		}
	}
}
