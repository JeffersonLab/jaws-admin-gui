import Dexie from './dexie-3.0.3.js';
import {AlarmClass, AlarmInstance, EffectiveRegistration, KafkaLogPosition} from "./entities.js";

const db = new Dexie("jaws");

db.version(1).stores({
    effectives: "name",
    classes: "name",
    instances: "name",
    positions: "name"
});

db.effectives.mapToClass(EffectiveRegistration);
db.classes.mapToClass(AlarmClass);
db.instances.mapToClass(AlarmInstance);
db.positions.mapToClass(KafkaLogPosition);

export default db;