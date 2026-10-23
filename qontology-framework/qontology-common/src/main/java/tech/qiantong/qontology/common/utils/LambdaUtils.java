/*
 * Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.
 *  *
 * Software Name: qKnow Knowledge Platform (Business Edition)
 * Software Copyright Registration No. 15980140
 *  *
 * [RIGHTS AND LICENSE STATEMENT]
 * This file contains non-public commercial source code of which Jiangsu Qiantong
 * Technology Co., Ltd. lawfully possesses complete intellectual property rights.
 *  *
 * Access and use are limited to entities or individuals who have signed a valid
 * commercial license agreement, within the scope stipulated in the agreement.
 * The "accessibility" of this source code is premised on lawful authorization
 * and does not constitute any form of transfer of intellectual property rights
 * or implied licensing.
 *  *
 * [PROHIBITIONS]
 * Unless explicitly agreed in the license agreement, the following acts in any
 * form are strictly prohibited:
 * 1. Copying, disseminating, disclosing, selling, renting, or redistributing
 * this source code;
 * 2. Providing the software's functionality to third parties via SaaS, PaaS,
 * cloud hosting, or other means;
 * 3. Using this software or its derivative versions to develop products that
 * compete with the Right Holder;
 * 4. Providing or displaying this source code or related technical information
 * to unauthorized third parties;
 * 5. Tampering with, circumventing, or destroying copyright notices, license
 * verifications, or other technical protection measures.
 *  *
 * [LEGAL LIABILITY]
 * Any unauthorized use constitutes an infringement of trade secrets and
 * intellectual property rights.
 *  *
 * The Right Holder will strictly pursue liability for breach of contract and
 * infringement in accordance with the commercial agreement and laws such as
 * the "Copyright Law of the People's Republic of China" and the "Anti-Unfair
 * Competition Law".
 *  *
 * ============================================================================
 *  *
 * Copyright (c) 2026 江苏千桐科技有限公司
 *  *
 * 软件名称：qKnow 知识平台（商业版） | 软著登字第15980140号
 *  *
 * 【权利与授权声明】
 * 本文件属于江苏千桐科技有限公司依法享有完全知识产权的非公开商业源代码。
 * 仅限已签署有效商业授权合同的单位或个人在约定范围内查阅和使用。
 * 源代码的“可访问性”均以合法授权为前提，不构成任何形式的知识产权转让或默示授权。
 *  *
 * 【禁止事项】
 * 除授权合同明确约定外，严禁任何形式的：
 * 1. 复制、传播、披露、出售、出租或再分发本源代码；
 * 2. 通过 SaaS、PaaS、云托管等方式向第三方提供本软件功能；
 * 3. 将本软件或其衍生版本用于开发与权利人构成竞争的产品；
 * 4. 向未授权第三方提供或展示本源代码或相关技术信息；
 * 5. 篡改、规避或破坏版权标识、授权校验及其他技术保护措施。
 *  *
 * 【法律责任】
 * 任何未经授权的利用行为，均构成对商业秘密及知识产权的侵害。
 * 权利人将依据商业合同及《中华人民共和国著作权法》《反不正当竞争法》
 * 等法律法规，严厉追究违约与侵权责任。
 */

package tech.qiantong.qontology.common.utils;

import java.io.Serializable;
import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解析器
 * @author wang
 * @date 2025/02/28 09:10
 **/
public class LambdaUtils {
    // 缓存 MethodHandles.Lookup 实例，以提高性能
    private static final Map<Class<?>, MethodHandles.Lookup> LOOKUP_CACHE = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface PropertyFunction<T, R> extends Serializable {
        R apply(T t);
    }

    public static <T> String getPropertyName(PropertyFunction<T, ?> func) {
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(func);

            String methodName = serializedLambda.getImplMethodName();
            if (methodName.startsWith("get")) {
                return resolveFieldName(methodName.substring(3));
            } else if (methodName.startsWith("is")) {
                return resolveFieldName(methodName.substring(2));
            }
            throw new IllegalArgumentException("Invalid getter method: " + methodName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract property name from lambda", e);
        }
    }

    /**
     * 获取一个类的 MethodHandles.Lookup 实例，用于访问类的成员。
     */
    private static MethodHandles.Lookup getLookup(Class<?> clazz) {
        return LOOKUP_CACHE.computeIfAbsent(clazz, k -> {
            try {
                // 尝试使用 privateLookupIn (Java 9+)，这是最标准的方式
                Method privateLookupInMethod = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
                return (MethodHandles.Lookup) privateLookupInMethod.invoke(null, clazz, MethodHandles.lookup());
            } catch (NoSuchMethodException e) {
                // 如果在 Java 8 上运行，privateLookupIn 不存在，则尝试通过反射获取构造函数
                try {
                    Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                    constructor.setAccessible(true);
                    return constructor.newInstance(clazz, MethodHandles.Lookup.PRIVATE);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to create Lookup for class " + clazz.getName(), ex);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create Lookup for class " + clazz.getName(), e);
            }
        });
    }


    private static String resolveFieldName(String methodSuffix) {
        return Character.toLowerCase(methodSuffix.charAt(0)) + methodSuffix.substring(1);
    }

    @SuppressWarnings("unchecked")
    public static <T> PropertyFunction<T, ?> getPropertyFunction(Class<T> entityClass, String propertyName) {
        if (entityClass == null || propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException("entityClass and propertyName must not be null or empty");
        }

        // 1. 查找 public getter 方法（getMethod 只找 public）
        String capitalized = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        Method getterMethod;
        try {
            getterMethod = entityClass.getMethod("get" + capitalized);
        } catch (NoSuchMethodException e) {
            try {
                getterMethod = entityClass.getMethod("is" + capitalized);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Could not find public getter method for property '" + propertyName +
                        "' in class " + entityClass.getName(), ex);
            }
        }

        try {
            // 使用标准 lookup（无需 private 权限，因为方法是 public）
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle methodHandle = lookup.unreflect(getterMethod);

            // SAM 接口的方法签名（擦除后）: (Object) -> Object
            MethodType samMethodType = MethodType.methodType(Object.class, Object.class);
            // 实际实现的方法签名: (T) -> R
            // e.g., (MyEntity) -> List<String>
            MethodType implMethodType = methodHandle.type();

            // 使用 altMetafactory 启用 FLAG_SERIALIZABLE
            CallSite callSite = LambdaMetafactory.altMetafactory(
                    lookup,
                    "apply",
                    MethodType.methodType(PropertyFunction.class),
                    samMethodType,
                    methodHandle,
                    implMethodType,
                    LambdaMetafactory.FLAG_SERIALIZABLE
            );

            return (PropertyFunction<T, ?>) callSite.getTarget().invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Failed to create PropertyFunction for method " + getterMethod, t);
        }
    }
}

