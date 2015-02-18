class Api::V1::AccountsController < ApplicationController
  respond_to :json

  def search
    byebug
    if request.headers["auth_token"] && request.headers["user_id"]

      ##### TODO: Check Authentication Tokens!!! ######

      first_name = request.headers["FirstName"]
      last_name = request.headers["LastName"]
      fhash = { first_name: first_name }
      lhash = { last_name: last_name }
      res = PersonAccount.fuzzy_search(fhash, false).fuzzy_search(lhash, false)
      respond_with res
    else
      render :json => request.headers["auth_token"]
    end
  end

  def create
    if request.headers["AuthToken"].eql? ENV["AuthToken"]
      pa = PersonAccount.new
      pa.first_name = request.headers["FirstName"]
      pa.last_name = request.headers["LastName"]
      pa.sf_id = request.headers["SalesforceID"]
      pa.save
    end
    respond_with []
  end

  def check
    respond_with request.headers["AuthToken"].eql? ENV["AuthToken"]
  end
end
