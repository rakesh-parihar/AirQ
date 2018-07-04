package com.airq.`interface`

/**
 * Interface for updating Webservice callbacks back to calling view
 *
 * @author Rakesh
 * @since 6/26/2018.
 */
interface WebResponseCallback{
    fun onSuccess(result:Any?)
    fun onFailure(reason:String?)
}