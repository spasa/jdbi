/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.postgres;

import java.util.concurrent.ConcurrentHashMap;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.internal.exceptions.Unchecked;
import org.postgresql.PGConnection;
import org.postgresql.util.PGobject;

/**
 * Handler for PostgreSQL custom types.
 */
public class PostgresTypes {
    private static final ConcurrentHashMap<Class<? extends PGobject>, String> TYPES = new ConcurrentHashMap<>();

    private PostgresTypes() {}

    /**
     * @param typeName the PostgreSQL type to register
     * @param clazz the class implementing the Java representation of the type;
     * this class must implement {@link PGobject}.
     */
    public static void registerCustomType(String typeName, Class<? extends PGobject> clazz) {
        TYPES.put(clazz, typeName);
    }

    public static String getTypeName(Class<? extends PGobject> clazz) {
        return TYPES.get(clazz);
    }

    /**
     * Add handler for each registered PostgreSQL custom type
     *
     * @param connection connection on which to add all registered PostgreSQL custom types
     */
    public static void addTypesToConnection(PGConnection connection) {
        TYPES.forEach((clazz, type) -> Unchecked.<String, Class>biConsumer(connection::addDataType).accept(type, clazz));
    }

    /**
     * Register array element type for each registered PostgreSQL custom type
     *
     * @param jdbi {@link Jdbi} on which to register array element type
     */
    public static void registerArrayTypes(Jdbi jdbi) {
        TYPES.forEach(jdbi::registerArrayType);
    }

}
