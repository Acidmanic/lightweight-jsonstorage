/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acidmanic.lightweight.jsonstorage.utility;

/**
 *
 * @author diego
 */
public interface TwoWayConverter<TypeA, TypeB> {

    TypeA from(TypeB value);

    TypeB to(TypeA value);

}
