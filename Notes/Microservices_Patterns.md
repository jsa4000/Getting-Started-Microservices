# 7 Useful Design Patterns for Microservices

 
Time and time again we’ve been seeing and successfully advising Simplicity Itself’s clients to use the same set of patterns when building their microservices-style systems.

Those patterns form the basis for the current portion of the Antifragile Software book and so I thought I’d share a quick summary of each here for everyone out there busily getting started or even if you’re some way down the road to your first microservice deployments.

Here are the top three of those design patterns straight from the stable of Domain Driven Design (DDD) as they also feature heavily in the Antifragile Software book:


## Pattern 1: Events

Events are immutable, sequenced past facts that are typically grouped into Commands (intends to change system state) and Queries (enquiring as to some facet of system state.

Events are distributed through ...


## Pattern 2: Event Streams

Streams of sequenced events are the key visualisation and comprehension dimension of the system.

Often implemented in their Reactive form to provide the useful feature of Back Pressure.

From the perspective of the calling service, it should always track its calls and be prepared to terminate them if the response takes too long. From the perspective of the called service, the API design should include the ability to send a response that indicates overload. This response, typically referred to as **back-pressure**, signals that the calling service should reduce or redirect its load.


## Pattern 3: Event Lakes

Events, being immutable facts, capture as much data about the event as possible. We have no way of knowing when or how that event and its data is going to be useful so… we store it, in sequence, often with a timestamp. Forever.

Sequenced events in these streams, which are sometimes hot (always available) or cold (can be available) collectively are stored and partitioned within a system we call an Event Lake.

Many of our clients ask what is the minimum set of data that should be stored in a Data Lake, and what is the Data Lake implemented as? Our advice is a Data Lake is a poor term and that it should be an Event Lake. Events being the minimum sets of information, or metadata, to be stored along with the raw data itself in the Lake.

An Event Lake is in fact a number of systems, each containing and representing various forms of Event Streams applying various quality-of-service processing, such as partitioning and security.

The main thing though it to always keep the raw raw events in sequence and available in a number of forms to support...


## Pattern 4: Event Sourcing and Projections


Event sourcing is the mechanism of taking those lakes of events and their constituent streams and applying projections to produce...

## Pattern 5: AggregatesAggregates are optimised, transactional representations of a current useful perspective on the system’s state. 

Optimised for update and then the distribution of a secondary set of events that are of use to produce...

## Pattern 6: ViewsViews are specialised representations of state, updated from events typically from aggregates but sometimes updated through direct events from the raw sources in a fast-data scenario.

Views are optimised to provide a particular answer to a question to support one or more...

## Pattern 7: GatewaysGateways provide a nice and convenient representation of the microservice-based system to the rest of the world and usually collaborate with one or more views and may also be sources of command events that are of use to the event lake...

What no REST?Some might be surprised that REST is not a fundamental pattern as covered here. The truth is that REST and any entity-centric interaction pattern is simply one option for event distribution.

In our experience this mode of integration is most useful when simply manipulating state, or when producing a gateway (API or otherwise) to the outside world where a simple request/response interaction is helpful.


