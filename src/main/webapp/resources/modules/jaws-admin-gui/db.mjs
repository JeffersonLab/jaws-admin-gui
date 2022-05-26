import Dexie from '../dexie-3.2.1/dexie.min.mjs';
import {AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, EffectiveRegistration, KafkaLogPosition} from "./entities.mjs";

const cacheVersion = '@CACHE_VERSION@';

// https://github.com/dexie/Dexie.js/issues/349#issuecomment-789173564
class CacheDB extends Dexie {
    constructor(name, cacheVersion) {
        super(name);

        this.cacheVersion = cacheVersion;
    }

    open() {
        if (this.isOpen()) return super.open();

        return Dexie.Promise.resolve()
            .then(() => Dexie.exists(this.name))
            .then((exists) => {
                if (!exists) {
                    // no need to check database version since it doesn't exist
                    console.log(`IndexDB: Schema doesn't exist, creating new`);
                    return;
                }

                // Open separate instance of dexie to get current database vers
                return new Dexie(this.name).open()
                    .then(async db => {
                        if (db.verno == this.cacheVersion) {
                            console.log(`IndexDB: Schema already up-to-date with version: ${this.cacheVersion}`);
                            // database up to date (or newer)
                            return db.close();
                        }

                        console.log(`IndexDB: Schema out of date, resetting all data. (currentVersion: ${db.verno}, expectedVersion: ${this.cacheVersion})`);
                        await db.close();
                        await db.delete();

                        // ensure delete was successful
                        const exists = await Dexie.exists(this.name);
                        if (exists) {
                            throw new Error('IndexDB: Failed to remove mock backend database.');
                        }
                    })
            })
            .then(() => super.open());
    }
}

const db = new CacheDB("jaws", cacheVersion);

db.version(cacheVersion).stores({
    categories: "name",
    classes: "name",
    effectives: "name",
    instances: "name",
    locations: "name",
    positions: "name"
});

db.categories.mapToClass(AlarmCategory);
db.classes.mapToClass(AlarmClass);
db.effectives.mapToClass(EffectiveRegistration);
db.instances.mapToClass(AlarmInstance);
db.locations.mapToClass(AlarmLocation);
db.positions.mapToClass(KafkaLogPosition);

export default db;