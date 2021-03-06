package de.danielbechler.diff

import spock.lang.Specification

import static de.danielbechler.diff.PhoneBookSetup.Contact

/**
 * Created by Daniel Bechler.
 */
class IncludingAnElementViaNodePathITCase extends Specification {

	def builder = ObjectDifferBuilder.startBuilding()
	def configurable = builder.configure()
	def working = PhoneBookSetup.getWorking()
	def base = PhoneBookSetup.getBase()
	def pathToContacts = NodePath.startBuilding().propertyName('contacts').build()
	def pathToContactKramer = NodePath.startBuildingFrom(pathToContacts).collectionItem(new Contact(id: 'kramer')).build()
	def pathToContactGeorge = NodePath.startBuildingFrom(pathToContacts).collectionItem(new Contact(id: 'george')).build()

	def 'should include its children as well'() {
		def includedNode = NodePath.with('contacts')

		given:
		  configurable.inclusion().toInclude().node(includedNode)

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild(includedNode).childCount() == 2
	}

	def 'should include its parents but not their children'() {
		given:
		  configurable.inclusion().toInclude().node(pathToContactKramer)

		when:
		  def node = builder.build().compare(working, base)

		then: "kramer should be included, because he has been explicitly included"
		  node.getChild(pathToContactKramer).changed

		and: "george should be excluded, since he has not been explicitly included"
		  node.getChild(pathToContactGeorge) == null
	}

	def 'ideas'() {
		configurable.inclusion().toInclude().node(pathToContactKramer)
	}
}
