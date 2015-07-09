class Api::V1::AccountsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def show
    account = Account.find_by(sf_id: params[:sf_id])
    if !account.nil?
      render json: account, status: :ok
    else
      api_message_response(404, "Account with that id does not exist.")
    end
  end

  def search
    first_name = request.params[:FirstName]
    last_name = request.params[:LastName]
    cursor = request.params[:cursor]
    result = Account.fuzzy_search({ FirstName: first_name, LastName: last_name }, false)
    result.each do |account|
      if account.sf_id.nil?
        account.sf_id = "None"
      end
    end
    unless cursor.nil?
      result = result[cursor.to_i, 20] # Hardcoded 20 pagination size
    end
    if result.nil?
      render json: [].to_json, status: :ok
    else
      render json: result.to_json(only: [:FirstName, :LastName, :Birthdate__c, :sf_id]), status: :ok
    end
  end

  def create
    if not valid_birthdate
      render json: { message: 'Invalid birthdate.' }, status: :bad_request
    end

    @account = Account.new(account_params)

    if @account.save
      render json: { message: 'Successfully saved account.' }, status: :ok
    else
      render json: { message: 'Error saving account: #{@account.errors.join(",")' } , status: :bad_request
    end

  end

  private

    def account_params
      # Figure out what params are mandatory and which are not
      params.require(:account).permit(:FirstName)
    end

end
