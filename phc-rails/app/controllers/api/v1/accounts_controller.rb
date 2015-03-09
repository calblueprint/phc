class Api::V1::AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token

  def show
    sf_id = params[:sf_id]
    respond_with Account.find_by sf_id: sf_id
  end

  def search
    # ActiveRecord::Base.connection.execute("SELECT set_limit(0.001);")
    auth_token = request.headers["HTTP_AUTH_TOKEN"]
    user_id = request.headers["HTTP_USER_ID"]
    if auth_token && user_id
      if user_authenticated?(user_id, auth_token)
        first_name = request.headers["HTTP_FIRSTNAME"]
        last_name = request.headers["HTTP_LASTNAME"]
        result = Account.fuzzy_search({:FirstName=>first_name, :LastName=>last_name}, false)
        respond_with result.to_json(:only => [:FirstName, :LastName, :Birthdate__c, :sf_id])
      end
    else
      respond_with "Error: Please include authentication token.", status: 401
    end
  end

  def create
    auth_token = request.headers["HTTP_AUTH_TOKEN"]
    user_id = request.headers["HTTP_USER_ID"]
    if auth_token && user_id
      if user_authenticated?(user_id, auth_token)
        person = Account.new
        person.FirstName = params[:first_name]
        person.LastName = params[:last_name]
        person.sf_id = request.headers[:salesforceid]
        person.save
        respond_with "Successfully saved user!", status: 200, location: root_url

        ##### TODO: POST TO SALESFORCE #####
      end
    else
      respond_with "Error: Please include authentication token.", status: 401, location: root_url
    end

  end

  private

    def user_authenticated?(id, token)
      user = User.find_by(id: id)
      if not user
        respond_with "Error: User ID not found in database.", status: 401
        false
      elsif not user.authenticated?(token)
        respond_with "Error: Invalid authentication token.", status: 401
        false
      else
        true
      end
    end

end
